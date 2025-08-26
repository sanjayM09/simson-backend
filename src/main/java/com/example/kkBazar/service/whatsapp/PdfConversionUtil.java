package com.example.kkBazar.service.whatsapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import org.apache.pdfbox.pdmodel.PDDocument;
import javax.sql.rowset.serial.SerialBlob;

public class PdfConversionUtil {

    public static Blob convertPdfUrlToBlob(String pdfUrl) throws IOException, SQLException {
        try (InputStream inputStream = new URL(pdfUrl).openStream()) {
            PDDocument document = PDDocument.load(inputStream);
            byte[] pdfBytes = getPdfBytes(document);
            return new SerialBlob(pdfBytes);
        } catch (Exception e) {
            throw new IOException("Error converting PDF from URL to Blob: " + e.getMessage(), e);
        }
    }

    private static byte[] getPdfBytes(PDDocument document) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }
}
    