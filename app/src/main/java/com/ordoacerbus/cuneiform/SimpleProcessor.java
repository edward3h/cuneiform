package com.ordoacerbus.cuneiform;

import java.nio.file.Path;
import java.util.Map;

public class SimpleProcessor implements Processor {
    private final Path input;

    public SimpleProcessor(Path input) {
        this.input = input;
    }

    @Override
    public Metadata processInto(Path outdir) {
        return new Metadata() {
            @Override
            public Path outDir() {
                return outdir;
            }

            @Override
            public Map<String, Object> asMap() {
                return Map.of();
            }

            @Override
            public String toString() {
                return "Skipping file %s".formatted(input);
            }
        };
    }
}
