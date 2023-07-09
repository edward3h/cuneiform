package com.ordoacerbus.cuneiform;

import de.androidpit.colorthief.ColorThief;
import org.apache.commons.io.output.NullWriter;
import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FactionProcessor extends PDFTextStripper implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FactionProcessor.class);
    private static final Pattern KEYWORDS = Pattern.compile("^KEYWORDS[^:]*:(.*)");

    private static final Color LIGHT = Color.WHITE;
    private static final Color DARK = Color.GRAY;

    private final Path input;
    private FactionBuilder factionBuilder;
    private DatasheetBuilder datasheetBuilder;
    private boolean start = true;
    private PageBuilder currentPage;
    private String previousText = "";
    private final Counter<String> factionCounter = new Counter<>();
    private final ImageRenderer renderer;
    private final ExecutorService executor;
    private final boolean htmlOnly;
    private PDDocument doc;

    public FactionProcessor(Path input, ImageRenderer renderer, ExecutorService executor, boolean htmlOnly) throws IOException {
        super();
        this.input = input;
        this.renderer = renderer;
        this.executor = executor;
        this.htmlOnly = htmlOnly;
    }

    @Override
    public Metadata processInto(Path outdir) throws IOException {
        LOGGER.info("Processing {}", input);
        factionBuilder = FactionBuilder.builder().keywords(new Counter<>());
        doc = PDDocument.load(input.toFile());
        if (!htmlOnly) {
            processImages(doc);
        }
        renderer.document(doc);
        writeText(doc, NullWriter.INSTANCE);
        var factionName = factionCounter.max().orElseThrow();
        factionBuilder.name(factionName);
        var dir = outdir.resolve(Util.safeName(factionName));
        factionBuilder.factionDir(dir);
        Files.createDirectories(dir);
        var futures = new ArrayList<Future<?>>();
        for (var page: factionBuilder.pages()) {
            futures.add(executor.submit(() -> {
                try {
                    writePageFile(dir, page);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }));
        }
        for (var future: futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return factionBuilder.build();
    }

    private void processImages(PDDocument doc) {
        // just trying to figure stuff out
        var catalog = doc.getDocumentCatalog();
        var number = 1;
        for (var page: catalog.getPages()) {
            var resources = page.getResources();
            processResources(number++, "", resources);
        }
    }

    private void processResources(int pageNumber, String prefix, PDResources resources) {
        try {
            for (var name : resources.getXObjectNames()) {
                var xobj = resources.getXObject(name);
                if (xobj instanceof PDImageXObject imageXObject) {
                    var originalImage = imageXObject.getImage();
                    var newImage = createReplacementImage(pageNumber, prefix, name, originalImage);
                    var newXObject = LosslessFactory.createFromImage(doc, newImage);
                    resources.put(name, newXObject);
                } else if (xobj instanceof PDFormXObject formXObject) {
                    var childResources = formXObject.getResources();
                    processResources(pageNumber, prefix + "_" + name.getName(), childResources);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static BufferedImage createReplacementImage(int pageNumber, String prefix, COSName name, BufferedImage originalImage) {
        var dominantColor = ColorThief.getColor(originalImage);
        var newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        var graphics = newImage.createGraphics();
        var drawColor = dominantColor == null ? Color.BLUE : new Color(dominantColor[0], dominantColor[1], dominantColor[2]);
        var b = getBrightness(drawColor);
        if (prefix.isBlank()) {
            drawColor = new Color(0,0,0,0);
        } else
        if (b < 0.35) {
            drawColor = DARK;
        } else
        if (b > 0.8) {
            drawColor = LIGHT;
        }
//        if (dominantColor[0] == 36) {
//            drawColor = Color.lightGray;
//        }
        graphics.setColor(drawColor);
        graphics.fillRect(0, 0, originalImage.getWidth(), originalImage.getHeight());
        LOGGER.debug("Image {} size {}x{} bright {}", prefix + "_" + name.getName(), originalImage.getWidth(), originalImage.getHeight(), b);

        return newImage;
    }

    private static float getBrightness(Color color) {
        var hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return hsb[2];
    }

    private void writePageFile(Path dir, Page p) throws IOException {
        if (p.title() == null) {
            return;
        }
        var filePath = dir.resolve(p.imagePath());
        renderer.renderPage(filePath, p.number());
    }

    @Override
    protected void startPage(PDPage page) {
        start = true;
        currentPage = PageBuilder.builder().number(getCurrentPageNo() - 1);
        var crop = page.getCropBox();
        if (crop.getHeight() > crop.getWidth()) {
            currentPage.type(PageType.General);
        } else {
            currentPage.type(PageType.Pending);
        }
    }

    @Override
    protected void endPage(PDPage ignore) throws IOException {
        var page = currentPage.build();
        switch (page.type()) {
            case DatasheetFront -> datasheetBuilder.pageFront(page);
            case DatasheetBack -> {
                datasheetBuilder.pageBack(page);
                factionBuilder.addDatasheets(datasheetBuilder.build());
                factionBuilder.keywords().add(datasheetBuilder.keywords());
                datasheetBuilder = null;
            }
        }
        factionBuilder.addPages(currentPage.build());
    }

    @Override
    protected void writeString(String text) throws IOException {
        if (start) {
            start = false;
            var title = text.strip().toLowerCase();
            currentPage.title(WordUtils.capitalizeFully(title));
            if (currentPage.type() == PageType.Pending) {
                if (title.contains("wargear options")) {
                    if (datasheetBuilder == null) {
                        throw new IllegalStateException("Options page but no datasheet?");
                    }
                    currentPage.type(PageType.DatasheetBack);
                    currentPage.title(datasheetBuilder.title() + " Options");
                } else if (title.contains("armoury")) {
                    currentPage.type(PageType.Armoury);
                } else {
                    currentPage.type(PageType.DatasheetFront);
                    datasheetBuilder = DatasheetBuilder.builder();
                    datasheetBuilder.title(currentPage.title());
                }
            }
        }
        if (currentPage.type() == PageType.DatasheetFront) {
            var matcher = KEYWORDS.matcher(text);
            if (matcher.matches()) {
                datasheetBuilder.addKeywords(
                        Stream.of(matcher.group(1).replaceAll("\\|[^:,]*:", ",").split(", "))
                                .map(String::strip)
                                .filter(s -> !s.isBlank())
                                .map(Keyword::new)
                );
            }
            if (previousText.contains("FACTION KEYWORDS")) {
                var factions = Stream.of(text.split(", "))
                        .map(String::strip)
                        .toList();
                datasheetBuilder.addFaction(factions);
                factionCounter.add(factions);
            }
        }
        previousText = text;
    }
}
