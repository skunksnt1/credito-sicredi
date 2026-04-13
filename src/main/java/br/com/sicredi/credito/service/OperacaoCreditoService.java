package br.com.sicredi.credito.service;

import br.com.sicredi.credito.client.ProdutoCreditoClient;
import br.com.sicredi.credito.dto.ContratacaoRequest;
import br.com.sicredi.credito.entity.TituloCredito;
import br.com.sicredi.credito.entity.VinculoSocioBeneficiario;
import br.com.sicredi.credito.enums.Segmento;
import br.com.sicredi.credito.repository.TituloCreditoRepository;
import br.com.sicredi.credito.repository.VinculoSocioBeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class OperacaoCreditoService {

    @Inject
    TituloCreditoRepository tituloCreditoRepository;

    @Inject
    VinculoSocioBeneficiarioRepository vinculoSocioBeneficiarioRepository;

    @Inject
    @RestClient
    ProdutoCreditoClient produtoCreditoClient;

    @Transactional
    public UUID contratar(ContratacaoRequest request) {
        if (request.getSegmento() == Segmento.AGRO) {
            if (request.getAreaBeneficiadaHa() == null
                    || request.getAreaBeneficiadaHa().compareTo(BigDecimal.ZERO) <= 0) {
                throw new WebApplicationException(
                        Response.status(422)
                                .entity("Operacoes AGRO exigem areaBeneficiadaHa preenchida e maior que zero")
                                .build());
            }
        }

        boolean permiteContratar;
        try {
            var resposta = produtoCreditoClient.verificarPermissaoContratacao(
                    request.getCodigoProdutoCredito(),
                    request.getSegmento().name(),
                    request.getValorOperacao().longValue()
            );
            permiteContratar = resposta.isPermiteContratar();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.SERVICE_UNAVAILABLE)
                            .entity("Servico de produtos indisponivel")
                            .build());
        }

        if (!permiteContratar) {
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Produto nao permite contratacao com os parametros informados")
                            .build());
        }

        TituloCredito titulo = new TituloCredito();
        titulo.setId(UUID.randomUUID());
        titulo.setIdAssociado(request.getIdAssociado());
        titulo.setValorOperacao(request.getValorOperacao());
        titulo.setSegmento(request.getSegmento());
        titulo.setCodigoProdutoCredito(request.getCodigoProdutoCredito());
        titulo.setCodigoConta(request.getCodigoConta());
        titulo.setAreaBeneficiadaHa(request.getAreaBeneficiadaHa());
        titulo.setDataContratacao(LocalDateTime.now());

        tituloCreditoRepository.persist(titulo);

        if (request.getSegmento() == Segmento.PJ) {
            VinculoSocioBeneficiario vinculo = new VinculoSocioBeneficiario();
            vinculo.setIdOperacaoCredito(titulo.getId());
            vinculo.setIdAssociado(request.getIdAssociado());
            vinculoSocioBeneficiarioRepository.persist(vinculo);
        }

        return titulo.getId();
    }
}
