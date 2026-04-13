package br.com.sicredi.credito.repository;

import br.com.sicredi.credito.entity.TituloCredito;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class TituloCreditoRepository implements PanacheRepositoryBase<TituloCredito, UUID> {
}
