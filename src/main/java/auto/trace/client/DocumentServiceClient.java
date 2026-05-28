package auto.trace.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "auto-document-service")
public interface DocumentServiceClient {

    @DeleteMapping("/documents/byUser/{userId}")
    void deleteAllDocumentsAndExpensesByUser(@PathVariable Long userId);
}
