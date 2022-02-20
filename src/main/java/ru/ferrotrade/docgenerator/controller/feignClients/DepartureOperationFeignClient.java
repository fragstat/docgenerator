package ru.ferrotrade.docgenerator.controller.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.ferrotrade.docgenerator.model.DepartureOperation;

@FeignClient(url = "http://localhost", name = "departureOperationClient")
public interface DepartureOperationFeignClient {

    @GetMapping("/api/departureOperation/{id}")
    DepartureOperation getDepartureOperation(@PathVariable("id") Long id);

}
