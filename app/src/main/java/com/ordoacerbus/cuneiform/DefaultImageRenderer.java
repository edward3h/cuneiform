package com.ordoacerbus.cuneiform;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultImageRenderer implements ImageRenderer {
    static final int DEFAULT_DPI = 200;
    private final int dpi;

    public DefaultImageRenderer(int dpi) {
        this.dpi = dpi;
    }

    public DefaultImageRenderer() {
        this(DEFAULT_DPI);
    }

    @Override
    public void renderPage(PDDocument doc, Path outputPath, int pageNumber) throws IOException {
        if (Files.exists(outputPath)) {
            return;
        }
        var renderer = new PDFRenderer(doc);
        var image = renderer.renderImageWithDPI(pageNumber, dpi, ImageType.RGB);
        ImageIOUtil.writeImage(image, outputPath.toString(), dpi, 0f);
        System.out.printf("Wrote %s%n", outputPath);
    }
}
