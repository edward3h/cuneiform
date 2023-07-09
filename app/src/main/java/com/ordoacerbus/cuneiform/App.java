/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.ordoacerbus.cuneiform;

import gg.jte.output.WriterOutput;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.ethelred.cuneiform.templates.StaticTemplates;
import org.ethelred.cuneiform.templates.Templates;
import org.ethelred.util.function.CheckedConsumer;
import org.ethelred.util.function.CheckedFunction;
import picocli.CommandLine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@CommandLine.Command(name = "cuneiform", description = "Generate web page of datasheets", mixinStandardHelpOptions = true)
public class App implements Callable<Integer> {

    @CommandLine.Parameters(arity = "1..*", description = "at least one pdf file or directory")
    List<Path> inputs = new ArrayList<>();

    @CommandLine.Option(names = {"--output-dir", "-o"})
    Path outdir = defaultOutdir();

    @CommandLine.Option(names = {"-i", "--html"})
    boolean htmlOnly;

    private ExecutorService executor;
    private Templates templates = new StaticTemplates();

    private Path defaultOutdir() {
        return Paths.get("");
    }

    public static void main(String[] args) {
        new CommandLine(new App()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
//        executor = Executors.newFixedThreadPool(4);
        executor = Executors.newSingleThreadExecutor();
        var pdfFiles = inputs.stream()
                .flatMap(CheckedFunction.unchecked(this::toPdfFiles))
                .collect(Collectors.toSet());
        pdfFiles.forEach(CheckedConsumer.unchecked(this::process));
        createIndex();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        return 0;
    }

    private void createIndex() throws IOException {
        new IndexProcessor(templates, outdir).process();
    }

    private void process(Path path) throws IOException {
        var processor = chooseProcessor(path);
        var meta = processor.processInto(outdir);
        if (meta instanceof Faction faction) {
            var indexPath = meta.outDir().resolve("index.html");
            try (var writer = Files.newBufferedWriter(indexPath, StandardCharsets.UTF_8)) {
                templates.faction(faction).render(new WriterOutput(writer));
            }
        }
    }

    private Processor chooseProcessor(Path path) throws IOException {
        try (var doc = PDDocument.load(path.toFile())) {
            for (var page: doc.getPages()) {
                var crop = page.getCropBox();
                if (crop.getWidth() > crop.getHeight()) {
                    // landscape format indicates a datasheet
                    return new FactionProcessor(path, new DefaultImageRenderer(), executor, htmlOnly);
                }
            }
        }
        return new SimpleProcessor(path);
    }

    private Stream<Path> toPdfFiles(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            return Stream.of(path);
        }
        return Files.find(path, 3, (p, attr) -> attr.isRegularFile() && p.toString().endsWith(".pdf"));
    }
}
