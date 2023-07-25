package com.ordoacerbus.cuneiform;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageRenderer {
    void renderPage(PDDocument doc, Path outputPath, int pageNumber) throws IOException;
}
