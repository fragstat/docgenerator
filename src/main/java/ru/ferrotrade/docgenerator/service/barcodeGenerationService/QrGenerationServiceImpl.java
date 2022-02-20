package ru.ferrotrade.docgenerator.service.barcodeGenerationService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;

@Service("qr")
public class QrGenerationServiceImpl implements BarcodeGenerationService {

    @Override
    public InputStream generateCode() {
        try {
            ByteArrayOutputStream baos = null;
            StringBuilder qr = new StringBuilder();
            char[] al = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
            for (int i = 0; i <= 12; i++) {
                qr.append(al[(int) (Math.random() * al.length)]);
            }
            BufferedImage imageBuff = MatrixToImageWriter.toBufferedImage(generateQRBarcodeImage(qr.toString(), 200));
            imageBuff = process(imageBuff, qr.toString());
            baos = new ByteArrayOutputStream();
            ImageIO.write(imageBuff, "png", baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BitMatrix generateQRBarcodeImage(String barcodeText, int size) throws Exception {
        QRCodeWriter code128Writer = new QRCodeWriter();
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        return code128Writer.encode(barcodeText, BarcodeFormat.QR_CODE, 200,
                200, hintMap);
    }

    private static BufferedImage process(BufferedImage old, String text) {
        int w = old.getWidth() ;
        int h = old.getHeight() ;
        BufferedImage img = new BufferedImage(
                w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(old, 0, 0, w, h, null);
        g2d.setPaint(Color.black);
        g2d.setFont(new Font("MS Shell Dlg 2", Font.PLAIN, 15));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (img.getWidth() - fm.stringWidth(text)) / 2 ;
        int y = 285 * 2 / 3;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return img;
    }

}
