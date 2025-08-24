package br.com.load.api2.messaging;

import br.com.load.api2.repo.BrandRepository;
import br.com.load.api2.repo.VehicleModelRepository;
import br.com.load.api2.service.FipeService;
import br.com.load.common.Brand;
import br.com.load.common.VehicleModel;
import br.com.load.common.VehicleType;
import br.com.load.fipe.FipeBrand;
import br.com.load.fipe.FipeModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BrandConsumer {

    private static final Logger LOG = Logger.getLogger(BrandConsumer.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    BrandRepository brandRepo;

    @Inject
    VehicleModelRepository modelRepo;

    @Inject
    FipeService fipe;

    @Incoming("brands-in")
    @Blocking
    @Transactional
    public void consume(Message<String> message) {
        String payload = null;
        try {

            payload = message.getPayload();

            Optional<IncomingRabbitMQMetadata> mdOpt = message.getMetadata(IncomingRabbitMQMetadata.class);

            mdOpt.ifPresent(md -> {

                String routingKey = md.getRoutingKey();
                Optional<String> corrId = md.getCorrelationId();
                Optional<ZonedDateTime> creationTime = md.getCreationTime(ZoneId.systemDefault());

                LOG.debugf("Msg recv — routingKey=%s, correlationId=%s, created at=%s", routingKey, corrId.orElse("N/A"), creationTime.map(ZonedDateTime::toString).orElse("N/A"));

            });

            FipeBrand brandMsg = mapper.readValue(payload, FipeBrand.class);

            Brand brand = brandRepo
                    .find("vehicleType = ?1 and code = ?2", VehicleType.cars.name(), brandMsg.code())
                    .firstResult();

            if (brand == null) {
                brand = new Brand();
                brand.vehicleType = VehicleType.cars.name();
                brand.code = brandMsg.code();
                brand.name = brandMsg.name();
                brandRepo.persist(brand);
                LOG.debugf("Marca criada: %s - %s", brand.code, brand.name);
            } else {
                brand.name = brandMsg.name();
                LOG.debugf("Marca atualizada: %s - %s", brand.code, brand.name);
            }

            List<FipeModel> models = fipe.fetchModels(VehicleType.cars, brandMsg.code(), null);

            for (FipeModel fm : models) {
                VehicleModel vm = modelRepo
                        .find("brand = ?1 and code = ?2", brand, fm.code())
                        .firstResult();
                if (vm == null) {
                    vm = new VehicleModel();
                    vm.brand = brand;
                    vm.code = fm.code();
                    vm.name = fm.name();
                    modelRepo.persist(vm);
                } else {
                    vm.name = fm.name();
                }
            }

            LOG.infof("Processado brand=%s, modelos=%d", brandMsg.code(), models.size());

        } catch (Exception e) {
            LOG.errorf(e, "Falha ao processar mensagem RabbitMQ. payload=%s", payload);
            try {
                message.nack(e);
            } catch (Exception ignore) {
            }
        }

    }

}
