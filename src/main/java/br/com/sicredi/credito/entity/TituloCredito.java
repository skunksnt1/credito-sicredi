package br.com.sicredi.credito.entity;

import br.com.sicredi.credito.enums.Segmento;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "titulo_credito")
public class TituloCredito extends PanacheEntityBase {

    @Id
    private UUID id;

    @Column(name = "id_associado", nullable = false)
    private Long idAssociado;

    @Column(name = "valor_operacao", nullable = false)
    private BigDecimal valorOperacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "segmento", nullable = false, length = 4)
    private Segmento segmento;

    @Column(name = "codigo_produto_credito", nullable = false, length = 10)
    private String codigoProdutoCredito;

    @Column(name = "codigo_conta", nullable = false, length = 10)
    private String codigoConta;

    @Column(name = "area_beneficiada_ha")
    private BigDecimal areaBeneficiadaHa;

    @Column(name = "data_contratacao", nullable = false)
    private LocalDateTime dataContratacao;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public LocalDateTime getDataContratacao() { return dataContratacao; }
    public void setDataContratacao(LocalDateTime dataContratacao) { this.dataContratacao = dataContratacao; }
}
