package com.ordoacerbus.cuneiform;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ImageRenderingTest {
    public static Stream<Arguments> renderImage() {
        var pdf = Paths.get("build/pdfs/C6o7G0zjRSxCUvhK.pdf").toAbsolutePath();
        return Stream.of(
            Arguments.of(pdf, 100),
            Arguments.of(pdf, 133),
            Arguments.of(pdf, 150),
            Arguments.of(pdf, 175),
            Arguments.of(pdf, DefaultImageRenderer.DEFAULT_DPI)
        );
    }

    @Disabled
    @ParameterizedTest
    @MethodSource
    void renderImage(Path pdf, int dpi) throws IOException {
        var renderer = new DefaultImageRenderer(dpi);
        var output = Path.of("build", "imageTest_%s_%d.png".formatted(pdf.getFileName().toString().substring(0, 16), dpi)).toAbsolutePath();
        try (var doc = PDDocument.load(pdf.toFile())) {
            renderer.renderPage(doc, output, 12);
        }
    }
}
