package br.com.sicredi.credito.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
public class AuthFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "api.token")
    String apiToken;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        // Libera acesso ao Swagger UI e OpenAPI spec
        if (path.startsWith("q/") || path.startsWith("openapi")) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"erro\": \"Token nao informado. Envie o header Authorization: Bearer <token>\"}")
                            .build());
            return;
        }

        String token = authHeader.substring(7);
        if (!apiToken.equals(token)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("{\"erro\": \"Token invalido\"}")
                            .build());
        }
    }
}
