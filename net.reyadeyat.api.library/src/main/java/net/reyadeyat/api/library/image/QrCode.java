package net.reyadeyat.api.library.image;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import net.reyadeyat.api.library.servlet.TransactionServlet;

public class QrCode extends TransactionServlet {
    
     public QrCode() {
        super("QrCode");
    }
    
    public void qr_code(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String qrcode = null;
        String[] parameter_list = {"qrcode"};
        JsonArray error_list = new JsonArray();
        if (isValid(parameter_list, req, error_list)) {
            qrcode = req.getParameter("qrcode");
        } else {
            JsonObject response = createJsonResponseObject(false, 400, 400, "errors", error_list);
            sendReponse(req, resp, response);
            return;
        }
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        JsonArray resultset = new JsonArray();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrcode, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage qr_image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(qr_image, "PNG", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        resultset.add("data:image/png;base64,"+Base64.getEncoder().encodeToString(imageBytes));
        if (error_list.size() == 0) {
            //con.commit();
            JsonObject response = createJsonResponseObject(true, 200, 200, "sucess");
            response.addProperty("INFO", "Exceution time");
            response.add("resultset", resultset);
            sendReponse(req, resp, response);
        } else {
            //con.rollback();
            error_list.add("rollback on errors");
            JsonObject response = createJsonResponseObject(false, 400, 400, "errors", error_list);
            sendReponse(req, resp, response);
        }
    }
}
