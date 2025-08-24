package br.com.load.api1.resource;

import br.com.load.api1.service.FipeService;
import br.com.load.common.VehicleType;
import br.com.load.fipe.FipeBrand;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/fipe")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FipeResource {

    @Inject
    private FipeService service;

    @GET
    @Path("/brands")
    @RolesAllowed({"user", "admin"})
    public List<FipeBrand> brands(@QueryParam("type") @DefaultValue("cars") VehicleType type, @QueryParam("reference") Integer reference) {
        return service.fetchBrands(type, reference);
    }

}
