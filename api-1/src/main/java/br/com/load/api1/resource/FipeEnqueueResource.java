package br.com.load.api1.resource;

import br.com.load.api1.messaging.BrandQueuePublisher;
import br.com.load.api1.service.FipeService;
import br.com.load.common.VehicleType;
import br.com.load.fipe.FipeBrand;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/admin/fipe")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FipeEnqueueResource {

    @Inject
    private FipeService fipe;

    @Inject
    private BrandQueuePublisher publisher;

    @POST
    @Path("/brands/enqueue")
    @RolesAllowed("admin")
    public Result enqueue(@QueryParam("type") @DefaultValue("cars") VehicleType type, @QueryParam("reference") Integer reference) {
        List<FipeBrand> brands = fipe.fetchBrands(type, reference);
        int count = publisher.enqueueAll(brands);
        return new Result("ok", type.name(), reference, count);
    }

    public record Result(String status, String type, Integer reference, int sent) {
    }

}
