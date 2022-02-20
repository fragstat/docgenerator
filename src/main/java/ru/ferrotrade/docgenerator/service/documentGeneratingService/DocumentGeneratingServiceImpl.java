package ru.ferrotrade.docgenerator.service.documentGeneratingService;

import lombok.SneakyThrows;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.ferrotrade.docgenerator.view.CertificateGenerationDataView;
import ru.ferrotrade.docgenerator.model.ChemicalElement;
import ru.ferrotrade.docgenerator.model.DepartureOperation;
import ru.ferrotrade.docgenerator.model.PhysicalParameter;
import ru.ferrotrade.docgenerator.model.Position;
import ru.ferrotrade.docgenerator.repository.PartDataRepo;
import ru.ferrotrade.docgenerator.repository.PlavDataRepo;
import ru.ferrotrade.docgenerator.service.barcodeGenerationService.BarcodeGenerationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class DocumentGeneratingServiceImpl implements DocumentGeneratingService {

    private static final String CERTIFICATE_EXAMPLE = "src/main/resources/documentsExamples/certificateExample.docx";
    private static final String PATH_OUT_TEST = "C:\\Users\\Сергей\\Desktop\\certificates\\";

    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    private static final Comparator<Position> positionComparator = Comparator.comparing(Position::getMark)
            .thenComparing(Position::getDiameter)
            .thenComparing(Position::getPart)
            .thenComparing(Position::getPlav)
            .thenComparing(Position::getPacking);

    private final PartDataRepo partDataRepo;

    private final PlavDataRepo plavDataRepo;

    private final BarcodeGenerationService barcodeGenerationService;

    public DocumentGeneratingServiceImpl(PartDataRepo partDataRepo, PlavDataRepo plavDataRepo,
                                         @Qualifier("qr") BarcodeGenerationService barcodeGenerationService) {
        this.partDataRepo = partDataRepo;
        this.plavDataRepo = plavDataRepo;
        this.barcodeGenerationService = barcodeGenerationService;
    }

    @Override
    public File generateCertificatesFromDepartureOperation(DepartureOperation operation) {
        List<CertificateGenerationDataView> dataViews = createCertificateDataViews(operation);
        String pathName = PATH_OUT_TEST + operation.getOperation_id();
        File folder = new File(pathName);
        folder.mkdirs();
        if (folder.exists()) {
            for (CertificateGenerationDataView dataView : dataViews) {
                dataView.order = dataViews.indexOf(dataView) + 1;
                generateCertificate(dataView,
                        pathName + "\\Сертификат A-" + dataView.departureOperation.getOperation_id() + "-" + dataView.order + ".docx");
            }
        }
        return folder;
    }

    @Override
    public boolean checkAbilityToGenerateCertificates(DepartureOperation operation) {
        return false;
    }

    @Override
    public boolean getDocs() {
        return false;
    }

    private static List<ChemicalElement> parseStringToElements(String elements) throws Exception {
        String[] elementsString = elements.trim().split(";");
        List<ChemicalElement> elementList = new ArrayList<>();
        for (String element : elementsString) {
            String[] params = element.trim().split(":");
            try {
                elementList.add(new ChemicalElement(params[0], params[1]));
            } catch (Exception e) {
                throw new IOException();
            }
        }
        return elementList;
    }

    private static List<PhysicalParameter> parseStringToParameters(String parameters) throws IOException {
        String[] parametersString = parameters.trim().split(";");
        List<PhysicalParameter> parameterList = new ArrayList<>();
        for (String parameter : parametersString) {
            String[] params = parameter.trim().split(":");
            try {
                parameterList.add(new PhysicalParameter(params[0], params[1]));
            } catch (Exception e) {
                throw new IOException();
            }
        }
        return parameterList;
    }

    private static List<CertificateGenerationDataView> createCertificateDataViews(DepartureOperation departureOperation) {
        TreeSet<Position> positions = new TreeSet<>(positionComparator);
        positions.addAll(departureOperation.getPositions());
        List<CertificateGenerationDataView> dataViews = new ArrayList<>();
        for (Position position : positions) {
            List<Position> departurePositions =
                    departureOperation.getPositions().stream().filter(p -> positionComparator.compare(p,
                            position) == 0).collect(Collectors.toList());
            Double weight = Precision.round(departurePositions.stream().mapToDouble(Position::getMass).sum(),2);
            dataViews.add(new CertificateGenerationDataView(position, departureOperation, weight,
                    departurePositions.size()));
        }
        return dataViews;
    }

    private void generateCertificate(CertificateGenerationDataView dataView, String path) {
        try (FileInputStream fileInputStream = new FileInputStream(CERTIFICATE_EXAMPLE)) {
            XWPFDocument certificate = new XWPFDocument(fileInputStream);
            fillStringData(certificate, dataView.departureOperation, dataView.etalon, dataView.order);
            fillTables(certificate, dataView);
            setQR(certificate);
            try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
              certificate.write(fileOutputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SneakyThrows
    private void setQR(XWPFDocument certificate) {
        XWPFRun qrRun = certificate.getParagraphs().get(1).createRun();
        certificate.getParagraphs().get(0).removeRun(0);
        certificate.getParagraphs().get(1).setAlignment(ParagraphAlignment.CENTER);
        try {
            qrRun.addPicture(barcodeGenerationService.generateCode(), Document.PICTURE_TYPE_PNG, "qr" , Units.toEMU(100),
                    Units.toEMU(100));
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
    }



    private void fillStringData(XWPFDocument certificate, DepartureOperation operation, Position position,
                                Integer order) {

        List<XWPFParagraph> paragraphs = certificate.getParagraphs();
        paragraphs.get(9).getRuns().get(0).setText(" " + operation.getCustomer());
        paragraphs.get(12).getRuns().get(0).setText("А-" + operation.getOperation_id() + "/" + order + " от " +
                sdf.format(Calendar.getInstance().getTime()) + ".");
        paragraphs.get(15).getRuns().get(1).setText(position.getMark() + ".");
        paragraphs.get(21).getRuns().get(0).setText(" " + "ГОСТ" + ".");

    }

    private void fillTables(XWPFDocument doc, CertificateGenerationDataView dataView) {
        Position p = dataView.etalon;

        List<XWPFTable> table = doc.getTables();
        XWPFTable mainTable = table.get(0);
        XWPFTableRow xwpfTableRow = mainTable.getRow(1);

        XWPFTableCell mark = xwpfTableRow.getCell(1);
        XWPFParagraph markParagraph = mark.addParagraph();
        markParagraph.setAlignment(ParagraphAlignment.CENTER);
        markParagraph.setFirstLineIndent(0);
        XWPFRun markRun = markParagraph.createRun();
        markRun.setText("Проволока " + p.getDiameter() + " " + p.getMark() + " ГОСТ");
        markRun.setFontFamily("Garamond");
        markRun.setFontSize(11);
        mark.removeParagraph(0);
        mark.setWidthType(TableWidthType.AUTO);

        XWPFTableCell diameter = xwpfTableRow.getCell(2);
        XWPFParagraph diameterParagraph = diameter.addParagraph();
        diameterParagraph.setAlignment(ParagraphAlignment.CENTER);
        diameterParagraph.setFirstLineIndent(0);
        XWPFRun diameterRun = diameterParagraph.createRun();
        diameterRun.setText(String.valueOf(Precision.round(Double.parseDouble(p.getDiameter()), 2)));
        diameterRun.setFontFamily("Garamond");
        diameterRun.setFontSize(11);
        diameter.removeParagraph(0);
        diameter.setWidthType(TableWidthType.AUTO);

        XWPFTableCell plav = xwpfTableRow.getCell(3);
        XWPFParagraph plavParagraph = plav.addParagraph();
        plavParagraph.setAlignment(ParagraphAlignment.CENTER);
        plavParagraph.setFirstLineIndent(0);
        XWPFRun plavRun = plavParagraph.createRun();
        plavRun.setText(p.getPlav());
        plavRun.setFontFamily("Garamond");
        plavRun.setFontSize(11);
        plav.removeParagraph(0);
        plav.setWidthType(TableWidthType.AUTO);

        XWPFTableCell part = xwpfTableRow.getCell(4);
        XWPFParagraph partParagraph = part.addParagraph();
        partParagraph.setAlignment(ParagraphAlignment.CENTER);
        partParagraph.setFirstLineIndent(0);
        XWPFRun partRun = partParagraph.createRun();
        partRun.setText(p.getPart());
        partRun.setFontFamily("Garamond");
        partRun.setFontSize(11);
        part.removeParagraph(0);
        part.setWidthType(TableWidthType.AUTO);

        XWPFTableCell amount = xwpfTableRow.getCell(5);
        XWPFParagraph amountParagraph = amount.addParagraph();
        amountParagraph.setAlignment(ParagraphAlignment.CENTER);
        amountParagraph.setFirstLineIndent(0);
        XWPFRun amountRun = amountParagraph.createRun();
        amountRun.setText(String.valueOf(dataView.amount));
        amountRun.setFontFamily("Garamond");
        amountRun.setFontSize(11);
        amount.removeParagraph(0);
        amount.setWidthType(TableWidthType.AUTO);

        XWPFTableCell weight = xwpfTableRow.getCell(6);
        XWPFParagraph weightParagraph = weight.addParagraph();
        weightParagraph.setAlignment(ParagraphAlignment.CENTER);
        weightParagraph.setFirstLineIndent(0);
        XWPFRun weightRun = weightParagraph.createRun();
        weightRun.setText(String.valueOf(dataView.weight));
        weightRun.setFontFamily("Garamond");
        weightRun.setFontSize(11);
        weight.removeParagraph(0);
        weight.setWidthType(TableWidthType.AUTO);

        XWPFTableCell packing = xwpfTableRow.getCell(7);
        XWPFParagraph packingParagraph = packing.addParagraph();
        packingParagraph.setAlignment(ParagraphAlignment.CENTER);
        packingParagraph.setFirstLineIndent(0);
        XWPFRun packingRun = packingParagraph.createRun();
        packingRun.setText(p.getPacking());
        packingRun.setFontFamily("Garamond");
        packingRun.setFontSize(11);
        packing.removeParagraph(0);
        packing.setWidthType(TableWidthType.AUTO);

        fillPlavTable(p.getPlav(), table.get(1));
        fillPartTable(p.getPart(), table.get(2));
    }

    private void fillPartTable(String part, XWPFTable table) {
        List<PhysicalParameter> parameters = getPhysicalParametersByPart(part);
        table.setCellMargins(0,0,0,0);

        table.setWidth(10348);
        table.getRow(0).getCell(0).setWidth("180");
        table.getRow(1).getCell(0).setWidth("180");
        table.getRow(0).getCell(0).getParagraphs().get(0).getRuns().get(0).setFontSize(11);
        table.getRow(1).getCell(0).getParagraphs().get(0).getRuns().get(0).setFontSize(11);

        for (PhysicalParameter parameter : parameters) {

            XWPFTableCell nameCell = table.getRow(0).createCell();
            XWPFParagraph nameParagraph = nameCell.addParagraph();
            nameParagraph.setAlignment(ParagraphAlignment.CENTER);
            nameParagraph.setFirstLineIndent(0);
            XWPFRun nameRun = nameParagraph.createRun();
            nameCell.setWidthType(TableWidthType.AUTO);
            nameRun.setText(parameter.name);
            nameRun.setBold(true);
            nameRun.setFontFamily("Garamond");
            nameRun.setFontSize(11);
            nameCell.removeParagraph(0);

            XWPFTableCell valueCell = table.getRow(1).createCell();
            XWPFParagraph valueParagraph = valueCell.addParagraph();
            valueParagraph.setAlignment(ParagraphAlignment.CENTER);
            valueParagraph.setFirstLineIndent(0);
            XWPFRun valueRun = valueParagraph.createRun();
            valueCell.setWidthType(TableWidthType.AUTO);
            valueRun.setText(parameter.value);
            valueRun.setFontFamily("Garamond");
            valueRun.setFontSize(11);
            valueCell.removeParagraph(0);
        }
    }

    private void fillPlavTable(String plav, XWPFTable table) {
        List<ChemicalElement> elements = getElementsByPlav(plav);
        table.setCellMargins(0,0,0,0);
        int fontSize = calculateFontSize(elements);

        table.setWidth(10348);
        table.getRow(0).getCell(0).setWidth("180");
        table.getRow(1).getCell(0).setWidth("180");
        table.getRow(0).getCell(0).getParagraphs().get(0).getRuns().get(0).setFontSize(fontSize);
        table.getRow(1).getCell(0).getParagraphs().get(0).getRuns().get(0).setFontSize(fontSize);
        for (ChemicalElement el : elements) {

            XWPFTableCell nameCell = table.getRow(0).createCell();
            XWPFParagraph nameParagraph = nameCell.addParagraph();
            nameParagraph.setAlignment(ParagraphAlignment.CENTER);
            nameParagraph.setFirstLineIndent(0);
            XWPFRun nameRun = nameParagraph.createRun();
            nameRun.setText(el.name);
            nameRun.setBold(true);
            nameRun.setFontFamily("Garamond");
            nameRun.setFontSize(fontSize);
            nameCell.removeParagraph(0);
            nameCell.setWidthType(TableWidthType.AUTO);

            XWPFTableCell valueCell = table.getRow(1).createCell();
            XWPFParagraph valueParagraph = valueCell.addParagraph();
            valueParagraph.setAlignment(ParagraphAlignment.CENTER);
            valueParagraph.setFirstLineIndent(0);
            XWPFRun valueRun = valueParagraph.createRun();
            valueRun.setText(el.value);
            valueRun.setFontFamily("Garamond");
            valueRun.setFontSize(fontSize);
            valueCell.removeParagraph(0);
            valueCell.setWidthType(TableWidthType.AUTO);
        }
    }

    private int calculateFontSize(List<ChemicalElement> list) {
        int size = list.size();
        if (size <= 12) {
            return 12;
        } else if (size <= 15) {
            return 11;
        } else if (size <= 16) {
            return 10;
        } else {
            return 9;
        }
    }

    private List<ChemicalElement> getElementsByPlav(String plav) {
        ArrayList<ChemicalElement> elements = new ArrayList<>();
        if (plavDataRepo.existsById(plav.trim())) {
            try {
                elements.addAll(parseStringToElements(plavDataRepo.findById(plav.trim()).get().getElementsValues()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return elements;
    }

    private List<PhysicalParameter> getPhysicalParametersByPart(String part) {
        ArrayList<PhysicalParameter> parameters = new ArrayList<>();
        if (partDataRepo.existsById(part.trim())) {
            try {
                parameters.addAll(parseStringToParameters(partDataRepo.findById(part.trim()).get().getParametersValues()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parameters;
    }
}
