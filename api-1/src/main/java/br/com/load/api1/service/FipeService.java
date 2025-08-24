package br.com.load.api1.service;

import br.com.load.common.VehicleType;
import br.com.load.fipe.FipeBrand;
import br.com.load.fipe.FipeClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class FipeService {

    @Inject
    @RestClient
    private FipeClient client;

    public List<FipeBrand> fetchBrands(VehicleType type, Integer reference) {
        return client.getBrands(type, reference).await().indefinitely();
    }

}
