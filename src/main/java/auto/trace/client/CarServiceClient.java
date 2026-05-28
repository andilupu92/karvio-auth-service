package auto.trace.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auto-car-service")
public interface CarServiceClient {

    @DeleteMapping("/cars/byUser/{userId}")
    void deleteAllCarsByUser(@PathVariable Long userId);
}
