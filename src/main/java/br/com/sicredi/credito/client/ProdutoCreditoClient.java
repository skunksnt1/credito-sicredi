package br.com.sicredi.credito.client;

import br.com.sicredi.credito.dto.PermiteContratarResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/produtoscredito")
public interface ProdutoCreditoClient {

    @GET
    @Path("/{codigoProduto}/permitecontratacao")
    PermiteContratarResponse verificarPermissaoContratacao(
            @PathParam("codigoProduto") String codigoProduto,
            @QueryParam("segmento") String segmento,
            @QueryParam("valorFinanciado") Long valorFinanciado
    );
}
