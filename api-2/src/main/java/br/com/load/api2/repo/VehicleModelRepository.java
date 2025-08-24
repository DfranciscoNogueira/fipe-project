package br.com.load.api2.repo;

import br.com.load.common.VehicleModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleModelRepository implements PanacheRepository<VehicleModel> {
}
