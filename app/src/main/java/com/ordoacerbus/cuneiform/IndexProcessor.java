package com.ordoacerbus.cuneiform;

import gg.jte.output.WriterOutput;
import org.ethelred.cuneiform.templates.Templates;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.TreeMap;

public class IndexProcessor {
    private static Logger LOGGER = LoggerFactory.getLogger(IndexProcessor.class);
    private final Templates templates;
    private final Path outdir;

    public IndexProcessor(Templates templates, Path outdir) {
        this.templates = templates;
        this.outdir = outdir;
    }

    public void process() throws IOException {
        Map<String, String> factionLinks = new TreeMap<>();
        Files.walkFileTree(outdir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!outdir.equals(file.getParent()) && "index.html".equals(file.getFileName().toString())) {
                    var title = getTitle(file);
                    if (!title.isBlank()) {
                        factionLinks.put(title, outdir.relativize(file).toString());
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        LOGGER.debug("faction links {}", factionLinks);
        try (var writer = Files.newBufferedWriter(outdir.resolve("index.html"), StandardCharsets.UTF_8)) {
            templates.index(factionLinks).render(new WriterOutput(writer));
        }
    }

    private String getTitle(Path file) throws IOException {
        var doc = Jsoup.parse(file.toFile());
        return doc.title();
    }
}
