package br.com.load.api2.service;

import br.com.load.common.VehicleType;
import br.com.load.fipe.FipeClient;
import br.com.load.fipe.FipeModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class FipeService {

    @Inject
    @RestClient
    private FipeClient client;

    public List<FipeModel> fetchModels(VehicleType type, String brandCode, Integer reference) {
        return client.getModels(type, brandCode, reference).await().indefinitely();
    }

}
