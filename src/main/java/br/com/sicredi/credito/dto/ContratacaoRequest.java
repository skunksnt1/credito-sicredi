package br.com.sicredi.credito.dto;

import br.com.sicredi.credito.enums.Segmento;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Dados para contratacao de uma operacao de credito")
public class ContratacaoRequest {

    @Schema(description = "Identificador do associado na base de dados", example = "12345", required = true)
    private Long idAssociado;

    @Schema(description = "Valor final a ser liberado na conta do associado", example = "5000", required = true)
    private BigDecimal valorOperacao;

    @Schema(description = "Segmento do credito: PF (Pessoa Fisica), PJ (Pessoa Juridica) ou AGRO (Agropecuario)", example = "PF", required = true)
    private Segmento segmento;

    @Schema(description = "Codigo de 3 caracteres do produto de credito", example = "101A", required = true)
    private String codigoProdutoCredito;

    @Schema(description = "Codigo numerico de 10 digitos da conta corrente", example = "0123456789", required = true)
    private String codigoConta;

    @Schema(description = "Area rural beneficiada em hectares (obrigatorio para AGRO)", example = "150.5")
    private BigDecimal areaBeneficiadaHa;

    public Long getIdAssociado() { return idAssociado; }
    public void setIdAssociado(Long idAssociado) { this.idAssociado = idAssociado; }
    public BigDecimal getValorOperacao() { return valorOperacao; }
    public void setValorOperacao(BigDecimal valorOperacao) { this.valorOperacao = valorOperacao; }
    public Segmento getSegmento() { return segmento; }
    public void setSegmento(Segmento segmento) { this.segmento = segmento; }
    public String getCodigoProdutoCredito() { return codigoProdutoCredito; }
    public void setCodigoProdutoCredito(String codigoProdutoCredito) { this.codigoProdutoCredito = codigoProdutoCredito; }
    public String getCodigoConta() { return codigoConta; }
    public void setCodigoConta(String codigoConta) { this.codigoConta = codigoConta; }
    public BigDecimal getAreaBeneficiadaHa() { return areaBeneficiadaHa; }
    public void setAreaBeneficiadaHa(BigDecimal areaBeneficiadaHa) { this.areaBeneficiadaHa = areaBeneficiadaHa; }
}
