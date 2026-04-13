package br.com.sicredi.credito.service;

import br.com.sicredi.credito.client.ProdutoCreditoClient;
import br.com.sicredi.credito.dto.ContratacaoRequest;
import br.com.sicredi.credito.dto.PermiteContratarResponse;
import br.com.sicredi.credito.entity.TituloCredito;
import br.com.sicredi.credito.entity.VinculoSocioBeneficiario;
import br.com.sicredi.credito.enums.Segmento;
import br.com.sicredi.credito.repository.TituloCreditoRepository;
import br.com.sicredi.credito.repository.VinculoSocioBeneficiarioRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class OperacaoCreditoServiceTest {

    @Inject
    OperacaoCreditoService service;

    @InjectMock
    TituloCreditoRepository tituloCreditoRepository;

    @InjectMock
    VinculoSocioBeneficiarioRepository vinculoSocioBeneficiarioRepository;

    @InjectMock
    @RestClient
    ProdutoCreditoClient produtoCreditoClient;

    private ContratacaoRequest requestBase;

    @BeforeEach
    void setUp() {
        requestBase = new ContratacaoRequest();
        requestBase.setIdAssociado(1L);
        requestBase.setValorOperacao(new BigDecimal("5000"));
        requestBase.setSegmento(Segmento.PF);
        requestBase.setCodigoProdutoCredito("101A");
        requestBase.setCodigoConta("0123456789");
    }

    @Test
    void deveCriarOperacaoComSucesso() {
        PermiteContratarResponse response = new PermiteContratarResponse();
        response.setPermiteContratar(true);
        when(produtoCreditoClient.verificarPermissaoContratacao(anyString(), anyString(), anyLong()))
                .thenReturn(response);

        var resultado = service.contratar(requestBase);

        assertNotNull(resultado);
        ArgumentCaptor<TituloCredito> captor = ArgumentCaptor.forClass(TituloCredito.class);
        verify(tituloCreditoRepository).persist(captor.capture());
        TituloCredito titulo = captor.getValue();
        assertEquals(1L, titulo.getIdAssociado());
        assertEquals(new BigDecimal("5000"), titulo.getValorOperacao());
        assertEquals(Segmento.PF, titulo.getSegmento());
        assertEquals("101A", titulo.getCodigoProdutoCredito());
        assertEquals("0123456789", titulo.getCodigoConta());
        assertNotNull(titulo.getDataContratacao());
        verify(vinculoSocioBeneficiarioRepository, never()).persist(any(VinculoSocioBeneficiario.class));
    }

    @Test
    void deveCriarOperacaoPJComVinculo() {
        requestBase.setSegmento(Segmento.PJ);
        requestBase.setCodigoProdutoCredito("202B");
        requestBase.setValorOperacao(new BigDecimal("10000"));

        PermiteContratarResponse response = new PermiteContratarResponse();
        response.setPermiteContratar(true);
        when(produtoCreditoClient.verificarPermissaoContratacao(anyString(), anyString(), anyLong()))
                .thenReturn(response);

        service.contratar(requestBase);

        verify(tituloCreditoRepository).persist(any(TituloCredito.class));
        ArgumentCaptor<VinculoSocioBeneficiario> captor = ArgumentCaptor.forClass(VinculoSocioBeneficiario.class);
        verify(vinculoSocioBeneficiarioRepository).persist(captor.capture());
        VinculoSocioBeneficiario vinculo = captor.getValue();
        assertEquals(1L, vinculo.getIdAssociado());
        assertNotNull(vinculo.getIdOperacaoCredito());
    }

    @Test
    void deveRejeitarAgroSemArea() {
        requestBase.setSegmento(Segmento.AGRO);
        requestBase.setCodigoProdutoCredito("903C");
        requestBase.setAreaBeneficiadaHa(null);

        WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> service.contratar(requestBase));

        assertEquals(422, exception.getResponse().getStatus());
        verify(tituloCreditoRepository, never()).persist(any(TituloCredito.class));
    }

    @Test
    void deveRejeitarAgroComAreaZero() {
        requestBase.setSegmento(Segmento.AGRO);
        requestBase.setCodigoProdutoCredito("903C");
        requestBase.setAreaBeneficiadaHa(BigDecimal.ZERO);

        WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> service.contratar(requestBase));

        assertEquals(422, exception.getResponse().getStatus());
        verify(tituloCreditoRepository, never()).persist(any(TituloCredito.class));
    }

    @Test
    void deveRejeitarQuandoProdutoNaoPermite() {
        PermiteContratarResponse response = new PermiteContratarResponse();
        response.setPermiteContratar(false);
        when(produtoCreditoClient.verificarPermissaoContratacao(anyString(), anyString(), anyLong()))
                .thenReturn(response);

        WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> service.contratar(requestBase));

        assertEquals(422, exception.getResponse().getStatus());
        verify(tituloCreditoRepository, never()).persist(any(TituloCredito.class));
    }

    @Test
    void deveRetornar503QuandoServicoIndisponivel() {
        when(produtoCreditoClient.verificarPermissaoContratacao(anyString(), anyString(), anyLong()))
                .thenThrow(new RuntimeException("Connection refused"));

        WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> service.contratar(requestBase));

        assertEquals(503, exception.getResponse().getStatus());
        verify(tituloCreditoRepository, never()).persist(any(TituloCredito.class));
    }
}
