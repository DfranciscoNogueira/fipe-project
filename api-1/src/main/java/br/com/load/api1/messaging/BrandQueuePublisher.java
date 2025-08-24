package br.com.load.api1.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class BrandQueuePublisher {

    private static final Logger LOG = Logger.getLogger(BrandQueuePublisher.class);

    @Inject
    @Channel("brands-out")
    private Emitter<String> emitter;

    @Inject
    private ObjectMapper mapper;

    public <T> int enqueueAll(List<T> items) {
        int count = 0;
        for (T it : items) {
            try {
                String json = mapper.writeValueAsString(it);
                emitter.send(json);
                count++;
            } catch (JsonProcessingException e) {
                LOG.error("Falha ao serializar item: " + e.getMessage());
            }
        }
        return count;
    }

}
