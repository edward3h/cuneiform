package com.ordoacerbus.cuneiform;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageRenderer {
    void document(PDDocument doc);

    void renderPage(Path filePath, int pageNumber) throws IOException;
}
