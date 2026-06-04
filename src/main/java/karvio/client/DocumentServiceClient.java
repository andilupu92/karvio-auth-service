package karvio.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "karvio-document-service")
public interface DocumentServiceClient {

    @DeleteMapping("/documents/byUser/{userId}")
    void deleteAllDocumentsAndExpensesByUser(@PathVariable Long userId);
}
