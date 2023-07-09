package com.ordoacerbus.cuneiform;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultImageRenderer implements ImageRenderer {
    private static final int DPI = 200;
    private PDFRenderer renderer;
    @Override
    public void document(PDDocument doc) {
        renderer = new PDFRenderer(doc);
    }

    @Override
    public void renderPage(Path filePath, int pageNumber) throws IOException {
        if (Files.exists(filePath)) {
            return;
        }
        var image = renderer.renderImageWithDPI(pageNumber, DPI, ImageType.RGB);
        ImageIOUtil.writeImage(image, filePath.toString(), DPI, 0f);
        System.out.printf("Wrote %s%n", filePath);
    }
}
