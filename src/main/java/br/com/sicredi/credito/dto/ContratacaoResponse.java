package br.com.sicredi.credito.dto;

import java.util.UUID;

public class ContratacaoResponse {

    private UUID idOperacaoCredito;

    public ContratacaoResponse() {}

    public ContratacaoResponse(UUID idOperacaoCredito) {
        this.idOperacaoCredito = idOperacaoCredito;
    }

    public UUID getIdOperacaoCredito() { return idOperacaoCredito; }
    public void setIdOperacaoCredito(UUID idOperacaoCredito) { this.idOperacaoCredito = idOperacaoCredito; }
}
