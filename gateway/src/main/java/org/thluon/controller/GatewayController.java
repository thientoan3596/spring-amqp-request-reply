package org.thluon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thluon.service.ItemService;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final ItemService itemService;
    @GetMapping("/test")
    public Mono<String> testGateway() {
        return Mono.just("OKE! All good!");
    }

    /**
     * Controller which will delegate job to item-service (in async manner)
     * @return
     */
    @GetMapping("/api/items")
    public Mono<ResponseEntity<?>> getItems() {
        return itemService.getItems();
    }

}
