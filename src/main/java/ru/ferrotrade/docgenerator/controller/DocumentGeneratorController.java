package ru.ferrotrade.docgenerator.controller;

import org.apache.poi.util.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ferrotrade.docgenerator.controller.feignClients.DepartureOperationFeignClient;
import ru.ferrotrade.docgenerator.model.DepartureOperation;
import ru.ferrotrade.docgenerator.service.awsS3service.AwsS3Service;
import ru.ferrotrade.docgenerator.service.certificateService.CertificateService;
import ru.ferrotrade.docgenerator.service.documentGeneratingService.DocumentGeneratingService;
import ru.ferrotrade.docgenerator.service.zeroCertificateGenerationService.ZeroCertificateGeneratingService;
import ru.ferrotrade.docgenerator.view.DocGenerationView;
import ru.ferrotrade.docgenerator.view.ZeroCertificateGenerationView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/docGenerator")
public class DocumentGeneratorController {

    final CertificateService certificateService;
    final DocumentGeneratingService documentGeneratingService;
    final DepartureOperationFeignClient departureOperationFeignClient;
    final AwsS3Service awsS3Service;

    public DocumentGeneratorController(CertificateService certificateService, DocumentGeneratingService documentGeneratingService, DepartureOperationFeignClient departureOperationFeignClient, AwsS3Service awsS3Service) {
        this.certificateService = certificateService;
        this.documentGeneratingService = documentGeneratingService;
        this.departureOperationFeignClient = departureOperationFeignClient;
        this.awsS3Service = awsS3Service;
    }

    @PostMapping("certificate")
    public ResponseEntity<String> generateZeroCertificate(@RequestBody ZeroCertificateGenerationView view, HttpServletResponse response) {
        certificateService.saveCertificate(view);
        return ResponseEntity.ok("/docGenerator/certificate/getZero/" + view.part.partId);
    }

    @GetMapping(value = "certificate/getZero/{id}")
    public void getZeroCertificate(@PathVariable String id, HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        try {
            IOUtils.copy(new ByteArrayInputStream(awsS3Service.downloadZeroCertificate(id)), response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @GetMapping(value = "certificate")
    public void generateDocument(@RequestBody DocGenerationView docGenerationView,
                                                            HttpServletResponse response) {
        DepartureOperation departureOperation =
                departureOperationFeignClient.getDepartureOperation(docGenerationView.departureOperation);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + docGenerationView.departureOperation + ".zip");

        ZipOutputStream zipOut = null;
        try {
            zipOut = new ZipOutputStream(response.getOutputStream());
            File certificate =
                    documentGeneratingService.generateCertificatesFromDepartureOperation(departureOperation);
            for (File f : Objects.requireNonNull(certificate.listFiles())) {
                awsS3Service.uploadFile(f);
                zipOut.putNextEntry(new ZipEntry(f.getName()));
                FileInputStream fis = new FileInputStream(f);

                IOUtils.copy(fis, zipOut);

                fis.close();
                zipOut.closeEntry();
            }
            zipOut.finish();
            zipOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "certificate/create/{id}")
    public void generateDocument(@PathVariable Long id, HttpServletResponse response) {
        DocGenerationView view = new DocGenerationView();
        view.departureOperation = id;
        generateDocument(view, response);
    }

    @GetMapping(value = "certificate/get/{id}")
    public void getDocument(@PathVariable String id, HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        try {
            IOUtils.copy(new ByteArrayInputStream(awsS3Service.downloadFile(id)), response.getOutputStream());
          } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("certificate/gost")
    public List<String> getGosts(@RequestParam(required = false) String mark) {
        return List.of("Проволка стальная сварочная ГОСТ 2246-70",
                "Проволока из высоколегированной корозионостойкой и жаростойкой стали ГОСТ 18143-72",
                "Проволока стальная наплавочная ГОСТ 10543-98",
                "Проволока стальная сварочная ТУ 14-1-1959-77",
                "Проволока стальная сварочная ТУ 14-1-1383-75",
                "Проволока стальная сварочная ТУ 1227-027-61668841-2020",
                "ТУ 14-1-2416-78",
                "ТУ 14-4345-2017",
                "ТУ 14-1-2989-80",
                "ТУ 14-1-2921-80",
                "ТУ 5.965-11610-96",
                "ТУ 14-1-1549-2015",
                "ТУ 14-1-1392-75");
    }

}
