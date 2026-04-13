package br.com.sicredi.credito.repository;

import br.com.sicredi.credito.entity.VinculoSocioBeneficiario;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VinculoSocioBeneficiarioRepository implements PanacheRepositoryBase<VinculoSocioBeneficiario, Long> {
}
