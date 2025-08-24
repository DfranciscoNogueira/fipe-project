package br.com.load.fipe;

import br.com.load.common.VehicleType;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/")
@RegisterRestClient(configKey = "fipe-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface FipeClient {

    @GET
    @Path("/{vehicleType}/brands")
    Uni<List<FipeBrand>> getBrands(@PathParam("vehicleType") VehicleType vehicleType, @QueryParam("reference") Integer reference);

    @GET
    @Path("/{vehicleType}/brands/{brandCode}/models")
    Uni<List<FipeModel>> getModels(@PathParam("vehicleType") VehicleType vehicleType, @PathParam("brandCode") String brandCode, @QueryParam("reference") Integer reference);

}
