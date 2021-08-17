package org.acme.products;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpServerRequest;

@Provider
public class LoggingFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Inject
    Config config;

    @ConfigProperty(name = "api.security.header.name")
    String header;

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        if (log.isDebugEnabled()) {
            final String method = context.getMethod();
            final String path = info.getPath();
            log.debug("Request for {} {}", method, path);
            log.debug("Request headers:");
            MultivaluedMap<String, String> rh = context.getHeaders();
            rh.forEach((key, value) -> log.debug("{} = {}", key, value));
        }

        Optional<String> secret = config.getOptionalValue("api.security.header.value", String.class);

        if (secret.isPresent()) {
            String incomingSecret = context.getHeaderString(header);
            String desiredSecret = secret.get().toString();

            log.info("enforcing " + header + " check using secret value: \"" + desiredSecret + "\"");

            if (incomingSecret.equals(desiredSecret)) {
                log.info("request passed security header check");
            } else {
                log.warn("request failed security header check. Incoming secret value was: \"" + incomingSecret + "\"");
                context.abortWith(Response.status(401).entity("Unauthorized: Invalid " + header).build());
            }
        } else {
            log.info("security header check is disabled");
        }
    }
}
