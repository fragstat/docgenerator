package ru.ferrotrade.docgenerator.service.zeroCertificateGenerationService;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import ru.ferrotrade.docgenerator.model.*;
import ru.ferrotrade.docgenerator.repository.PartDataRepo;
import ru.ferrotrade.docgenerator.repository.PlavDataRepo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ZeroCertificateGeneratingServiceImpl implements ZeroCertificateGeneratingService {

    private static final String CERTIFICATE_EXAMPLE = "src/main/resources/documentsExamples/zeroCertificateExample.docx";
    private static final String PATH_OUT_TEST = "src/main/resources/";

    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    private static final Comparator<Position> positionComparator = Comparator.comparing(Position::getMark)
            .thenComparing(Position::getDiameter)
            .thenComparing(Position::getPart)
            .thenComparing(Position::getPlav)
            .thenComparing(Position::getPacking);

    private final PartDataRepo partDataRepo;

    private final PlavDataRepo plavDataRepo;

    public ZeroCertificateGeneratingServiceImpl(PartDataRepo partDataRepo, PlavDataRepo plavDataRepo) {
        this.partDataRepo = partDataRepo;
        this.plavDataRepo = plavDataRepo;
    }

    @Override
    public File generateCertificatesFromCertificate(Certificate certificate) {
        String pathName = PATH_OUT_TEST + certificate.getId() + "/";
        File folder = new File(pathName);
        folder.mkdirs();
        generateCertificate(certificate, pathName);
        return Objects.requireNonNull(folder.listFiles())[0];
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

    private void generateCertificate(Certificate cert, String path) {
        try (FileInputStream fileInputStream = new FileInputStream(CERTIFICATE_EXAMPLE)) {
            XWPFDocument certificate = new XWPFDocument(fileInputStream);
            String[] gostArray = cert.getGost().split(" ");
            String gostString = gostArray[gostArray.length - 2] + " " + gostArray[gostArray.length - 1];
            fillStringData(certificate, "", String.valueOf(cert.getId()), cert.getDate(), cert.getMark(),
                    cert.getGost(), gostString);
            fillTables(certificate, gostString, cert.getDiameter(), cert.getMark(), cert.getPlav(), cert.getPart(),
                    cert.getPacking());
            try (FileOutputStream fileOutputStream = new FileOutputStream(path + cert.getPart() + ".docx")) {
              certificate.write(fileOutputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readQR(ByteArrayInputStream is) throws NotFoundException, IOException {
        BufferedImage img = ImageIO.read(is);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(img)));
        Result result = new MultiFormatReader().decode(binaryBitmap);
        return result.getText();
    }

    private void fillStringData(XWPFDocument certificate, String customer, String id, Date date, String mark,
                                String gost, String gostString) {
        List<XWPFParagraph> paragraphs = certificate.getParagraphs();
        paragraphs.get(17).getRuns().get(0).setText(" " + customer);
        paragraphs.get(20).getRuns().get(0).setText("А-" + id + " от " +
                sdf.format(date) + ".");
        paragraphs.get(23).getRuns().get(0).setText(gost + ".");
        paragraphs.get(29).getRuns().get(0).setText(" " + gostString + ".");
    }

    private void fillTables(XWPFDocument doc, String gost, String diameter, String mark, String plav, String part,
                            String packing) {
        List<XWPFTable> table = doc.getTables();
        XWPFTable mainTable = table.get(0);
        XWPFTableRow xwpfTableRow = mainTable.getRow(1);

        XWPFTableCell markCell = xwpfTableRow.getCell(1);
        XWPFParagraph markParagraph = markCell.addParagraph();
        markParagraph.setAlignment(ParagraphAlignment.CENTER);
        markParagraph.setFirstLineIndent(0);
        XWPFRun markRun = markParagraph.createRun();
        markRun.setText("Проволока " + diameter + " " + mark + " " + gost);
        markRun.setFontFamily("Garamond");
        markRun.setFontSize(11);
        markCell.removeParagraph(0);
        markCell.setWidthType(TableWidthType.AUTO);

        XWPFTableCell diameterCell = xwpfTableRow.getCell(2);
        XWPFParagraph diameterParagraph = diameterCell.addParagraph();
        diameterParagraph.setAlignment(ParagraphAlignment.CENTER);
        diameterParagraph.setFirstLineIndent(0);
        XWPFRun diameterRun = diameterParagraph.createRun();
        diameterRun.setText(diameter);
        diameterRun.setFontFamily("Garamond");
        diameterRun.setFontSize(11);
        diameterCell.removeParagraph(0);
        diameterCell.setWidthType(TableWidthType.AUTO);

        XWPFTableCell plavCell = xwpfTableRow.getCell(3);
        XWPFParagraph plavParagraph = plavCell.addParagraph();
        plavParagraph.setAlignment(ParagraphAlignment.CENTER);
        plavParagraph.setFirstLineIndent(0);
        XWPFRun plavRun = plavParagraph.createRun();
        plavRun.setText(plav);
        plavRun.setFontFamily("Garamond");
        plavRun.setFontSize(11);
        plavCell.removeParagraph(0);
        plavCell.setWidthType(TableWidthType.AUTO);

        XWPFTableCell partCell = xwpfTableRow.getCell(4);
        XWPFParagraph partParagraph = partCell.addParagraph();
        partParagraph.setAlignment(ParagraphAlignment.CENTER);
        partParagraph.setFirstLineIndent(0);
        XWPFRun partRun = partParagraph.createRun();
        partRun.setText(part);
        partRun.setFontFamily("Garamond");
        partRun.setFontSize(11);
        partCell.removeParagraph(0);
        partCell.setWidthType(TableWidthType.AUTO);

        XWPFTableCell amount = xwpfTableRow.getCell(5);
        XWPFParagraph amountParagraph = amount.addParagraph();
        amountParagraph.setAlignment(ParagraphAlignment.CENTER);
        amountParagraph.setFirstLineIndent(0);
        XWPFRun amountRun = amountParagraph.createRun();
        amountRun.setText("");
        amountRun.setFontFamily("Garamond");
        amountRun.setFontSize(11);
        amount.removeParagraph(0);
        amount.setWidthType(TableWidthType.AUTO);

        XWPFTableCell weight = xwpfTableRow.getCell(6);
        XWPFParagraph weightParagraph = weight.addParagraph();
        weightParagraph.setAlignment(ParagraphAlignment.CENTER);
        weightParagraph.setFirstLineIndent(0);
        XWPFRun weightRun = weightParagraph.createRun();
        weightRun.setText("");
        weightRun.setFontFamily("Garamond");
        weightRun.setFontSize(11);
        weight.removeParagraph(0);
        weight.setWidthType(TableWidthType.AUTO);

        XWPFTableCell packingCell = xwpfTableRow.getCell(7);
        XWPFParagraph packingParagraph = packingCell.addParagraph();
        packingParagraph.setAlignment(ParagraphAlignment.CENTER);
        packingParagraph.setFirstLineIndent(0);
        XWPFRun packingRun = packingParagraph.createRun();
        packingRun.setText(packing);
        packingRun.setFontFamily("Garamond");
        packingRun.setFontSize(11);
        packingCell.removeParagraph(0);
        packingCell.setWidthType(TableWidthType.AUTO);

        fillPlavTable(plav, table.get(1));
        fillPartTable(part, table.get(2));
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
