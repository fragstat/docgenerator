package ru.ferrotrade.docgenerator.controller;

import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.poi.util.IOUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ferrotrade.docgenerator.controller.feignClients.DepartureOperationFeignClient;
import ru.ferrotrade.docgenerator.model.DepartureOperation;
import ru.ferrotrade.docgenerator.service.documentGeneratingService.DocumentGeneratingService;
import ru.ferrotrade.docgenerator.view.DocGenerationResultView;
import ru.ferrotrade.docgenerator.view.DocGenerationView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/docGenerator")
public class DocumentGeneratorController {

    final DocumentGeneratingService documentGeneratingService;
    final DepartureOperationFeignClient departureOperationFeignClient;

    public DocumentGeneratorController(DocumentGeneratingService documentGeneratingService, DepartureOperationFeignClient departureOperationFeignClient) {
        this.documentGeneratingService = documentGeneratingService;
        this.departureOperationFeignClient = departureOperationFeignClient;
    }

    @SneakyThrows
    @GetMapping(value = "certificate")
    public void generateDocument(@RequestBody DocGenerationView docGenerationView,
                                                            HttpServletResponse response) {

        DepartureOperation departureOperation =
                departureOperationFeignClient.getDepartureOperation(docGenerationView.departureOperation);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + docGenerationView.departureOperation + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
        File certificate =
                documentGeneratingService.generateCertificatesFromDepartureOperation(departureOperation);
        for (File f : Objects.requireNonNull(certificate.listFiles())) {
            zipOut.putNextEntry(new ZipEntry(f.getName()));
            FileInputStream fis = new FileInputStream(f);

            IOUtils.copy(fis, zipOut);

            fis.close();
            zipOut.closeEntry();
        }
        zipOut.finish();
        zipOut.close();
    }

    @GetMapping(value = "certificate/{id}")
    public void generateDocument(@PathVariable Long id, HttpServletResponse response) {
        DocGenerationView view = new DocGenerationView();
        view.departureOperation = id;
        generateDocument(view, response);
    }

    @KafkaListener(topics = "generateDoc")
    private void getViewFromKafka(ConsumerRecord<UUID, DocGenerationView> record) {
        DepartureOperation departureOperation =
                departureOperationFeignClient.getDepartureOperation(record.value().departureOperation);
        documentGeneratingService.generateCertificatesFromDepartureOperation(departureOperation);
    }

}
