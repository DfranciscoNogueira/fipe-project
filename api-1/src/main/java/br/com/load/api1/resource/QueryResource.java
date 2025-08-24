package br.com.load.api1.resource;

import br.com.load.api1.cache.RedisCache;
import br.com.load.api1.repo.BrandRepository;
import br.com.load.api1.repo.VehicleModelRepository;
import br.com.load.common.Brand;
import br.com.load.common.VehicleModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QueryResource {

    @Inject
    private BrandRepository brandRepo;

    @Inject
    private VehicleModelRepository modelRepo;

    @Inject
    private RedisCache cache;

    @Inject
    private ObjectMapper mapper;

    @GET
    @Path("/brands")
    @RolesAllowed({"user", "admin"})
    public List<Brand> brands() {
        String key = "brands::all";
        return cache.get(key)
                .map(json -> {
                    try {
                        return mapper.readValue(json, new TypeReference<List<Brand>>() {
                        });
                    } catch (Exception e) {
                        return brandRepo.listAll();
                    }
                })
                .orElseGet(() -> {
                    List<Brand> list = brandRepo.listAll();
                    try {
                        cache.set(key, mapper.writeValueAsString(list), Duration.ofMinutes(5));
                    } catch (Exception ignored) {
                    }
                    return list;
                });
    }

    @GET
    @Path("/vehicles")
    @RolesAllowed({"user", "admin"})
    public List<VehicleModel> vehiclesByBrand(@QueryParam("brandCode") String brandCode) {
        String key = "vehicles::" + brandCode;
        return cache.get(key)
                .map(json -> {
                    try {
                        return mapper.readValue(json, new TypeReference<List<VehicleModel>>() {
                        });
                    } catch (Exception e) {
                        return fetchVehicles(brandCode);
                    }
                })
                .orElseGet(() -> {
                    List<VehicleModel> list = fetchVehicles(brandCode);
                    try {
                        cache.set(key, mapper.writeValueAsString(list), Duration.ofMinutes(5));
                    } catch (Exception ignored) {
                    }
                    return list;
                });
    }

    private List<VehicleModel> fetchVehicles(String brandCode) {
        Brand b = brandRepo.find("code", brandCode).firstResult();
        if (b == null) throw new NotFoundException("Marca não encontrada: " + brandCode);
        return modelRepo.list("brand", b);
    }

    public static class UpdateVehicleDTO {
        public String name;
        public String observations;
    }

    @PUT
    @Path("/vehicles/{id}")
    @Transactional
    @RolesAllowed("admin")
    public VehicleModel updateVehicle(@PathParam("id") Long id, UpdateVehicleDTO dto) {
        VehicleModel vm = modelRepo.findById(id);
        if (vm == null) throw new NotFoundException("Veículo não encontrado: " + id);
        if (dto.name != null && !dto.name.isBlank()) vm.name = dto.name;
        vm.observations = dto.observations;
        // Invalidação simples de cache (TODO: melhorar isso 'Diego')
        if (vm.brand != null) {
            try {
                cache.set("vehicles::" + vm.brand.code, "[]", Duration.ofSeconds(1));
            } catch (Exception ignored) {
            }
        }
        return vm;
    }

}
