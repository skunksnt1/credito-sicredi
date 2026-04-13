package br.com.sicredi.credito.controller;

import br.com.sicredi.credito.client.ProdutoCreditoClient;
import br.com.sicredi.credito.dto.PermiteContratarResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
class OperacaoCreditoControllerTest {

    @InjectMock
    @RestClient
    ProdutoCreditoClient produtoCreditoClient;

    @BeforeEach
    void setUp() {
        PermiteContratarResponse response = new PermiteContratarResponse();
        response.setPermiteContratar(true);
        when(produtoCreditoClient.verificarPermissaoContratacao(anyString(), anyString(), anyLong()))
                .thenReturn(response);
    }

    @Test
    void deveContratarERetornarId() {
        given()
                .contentType("application/json")
                .body("""
                    {
                        "idAssociado": 1,
                        "valorOperacao": 5000,
                        "segmento": "PF",
                        "codigoProdutoCredito": "101A",
                        "codigoConta": "0123456789"
                    }
                    """)
        .when()
                .post("/api/operacoes-credito")
        .then()
                .statusCode(201)
                .body("idOperacaoCredito", notNullValue());
    }

    @Test
    void deveConsultarOperacaoCriada() {
        String idOperacao = given()
                .contentType("application/json")
                .body("""
                    {
                        "idAssociado": 1,
                        "valorOperacao": 5000,
                        "segmento": "PF",
                        "codigoProdutoCredito": "101A",
                        "codigoConta": "0123456789"
                    }
                    """)
        .when()
                .post("/api/operacoes-credito")
        .then()
                .statusCode(201)
                .extract().path("idOperacaoCredito");

        given()
        .when()
                .get("/api/operacoes-credito/" + idOperacao)
        .then()
                .statusCode(200)
                .body("idOperacaoCredito", equalTo(idOperacao))
                .body("idAssociado", equalTo(1))
                .body("valorOperacao", equalTo(5000.0F))
                .body("segmento", equalTo("PF"))
                .body("codigoProdutoCredito", equalTo("101A"))
                .body("codigoConta", equalTo("0123456789"))
                .body("dataContratacao", notNullValue());
    }

    @Test
    void deveRetornar404ParaOperacaoInexistente() {
        given()
        .when()
                .get("/api/operacoes-credito/" + UUID.randomUUID())
        .then()
                .statusCode(404);
    }
}
