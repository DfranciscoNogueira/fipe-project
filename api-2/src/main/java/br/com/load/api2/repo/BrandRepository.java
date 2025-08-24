package br.com.load.api2.repo;

import br.com.load.common.Brand;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BrandRepository implements PanacheRepository<Brand> {
}
