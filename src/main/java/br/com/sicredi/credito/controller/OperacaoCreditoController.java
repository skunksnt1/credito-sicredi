package br.com.sicredi.credito.controller;

import br.com.sicredi.credito.dto.ConsultaResponse;
import br.com.sicredi.credito.dto.ContratacaoRequest;
import br.com.sicredi.credito.dto.ContratacaoResponse;
import br.com.sicredi.credito.entity.TituloCredito;
import br.com.sicredi.credito.repository.TituloCreditoRepository;
import br.com.sicredi.credito.service.OperacaoCreditoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/operacoes-credito")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Operacoes de Credito", description = "Endpoints para contratacao e consulta de operacoes de credito")
@SecurityScheme(
        securitySchemeName = "bearer",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "Token",
        description = "Informe o token de acesso. Token padrao: sicredi-credito-2026"
)
@SecurityRequirement(name = "bearer")
public class OperacaoCreditoController {

    @Inject
    OperacaoCreditoService service;

    @Inject
    TituloCreditoRepository tituloCreditoRepository;

    @POST
    @Operation(summary = "Contratar operacao de credito", description = "Cria um titulo de credito na base de dados apos validar as regras de negocio. Para segmento AGRO, o campo areaBeneficiadaHa e obrigatorio e deve ser maior que zero. Para segmento PJ, um vinculo com o socio beneficiario sera criado automaticamente.")
    @RequestBody(description = "Dados da contratacao de credito", required = true, content = @Content(schema = @Schema(implementation = ContratacaoRequest.class)))
    @APIResponse(responseCode = "201", description = "Operacao contratada com sucesso", content = @Content(schema = @Schema(implementation = ContratacaoResponse.class)))
    @APIResponse(responseCode = "401", description = "Token nao informado")
    @APIResponse(responseCode = "403", description = "Token invalido")
    @APIResponse(responseCode = "422", description = "Contratacao recusada - AGRO sem area beneficiada ou produto nao permite contratacao")
    @APIResponse(responseCode = "503", description = "Servico de validacao de produtos indisponivel")
    public Response contratar(ContratacaoRequest request) {
        UUID id = service.contratar(request);
        return Response.status(Response.Status.CREATED)
                .entity(new ContratacaoResponse(id))
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Consultar operacao de credito", description = "Retorna todos os dados de uma operacao de credito a partir do seu identificador unico (UUID)")
    @APIResponse(responseCode = "200", description = "Operacao encontrada com sucesso", content = @Content(schema = @Schema(implementation = ConsultaResponse.class)))
    @APIResponse(responseCode = "401", description = "Token nao informado")
    @APIResponse(responseCode = "403", description = "Token invalido")
    @APIResponse(responseCode = "404", description = "Operacao nao encontrada")
    public Response consultar(@Parameter(description = "Identificador unico da operacao de credito (UUID)", required = true) @PathParam("id") UUID id) {
        TituloCredito titulo = tituloCreditoRepository.findById(id);
        if (titulo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ConsultaResponse response = new ConsultaResponse();
        response.setIdOperacaoCredito(titulo.getId());
        response.setIdAssociado(titulo.getIdAssociado());
        response.setValorOperacao(titulo.getValorOperacao());
        response.setSegmento(titulo.getSegmento());
        response.setCodigoProdutoCredito(titulo.getCodigoProdutoCredito());
        response.setCodigoConta(titulo.getCodigoConta());
        response.setAreaBeneficiadaHa(titulo.getAreaBeneficiadaHa());
        response.setDataContratacao(titulo.getDataContratacao());

        return Response.ok(response).build();
    }
}
