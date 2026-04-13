package br.com.sicredi.credito.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "vinculo_socio_beneficiario")
public class VinculoSocioBeneficiario extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_operacao_credito", nullable = false)
    private UUID idOperacaoCredito;

    @Column(name = "id_associado", nullable = false)
    private Long idAssociado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getIdOperacaoCredito() { return idOperacaoCredito; }
    public void setIdOperacaoCredito(UUID idOperacaoCredito) { this.idOperacaoCredito = idOperacaoCredito; }
    public Long getIdAssociado() { return idAssociado; }
    public void setIdAssociado(Long idAssociado) { this.idAssociado = idAssociado; }
}
