package com.ordoacerbus.cuneiform;

import io.soabase.recordbuilder.core.RecordBuilderFull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.ordoacerbus.cuneiform.Keyword.kw;

@RecordBuilderFull
public record Faction(String name, List<Datasheet> datasheets, List<Page> pages, Counter<Keyword> keywords, Path factionDir) implements Metadata {
    private static final Logger LOGGER = LoggerFactory.getLogger(Faction.class);

    public Faction {
        datasheets = new ArrayList<>(datasheets);
        datasheets.sort(Comparator.comparing(Datasheet::title));
        var datasheetsCount = datasheets.size();
        LOGGER.debug("datasheets {} keywords {}", datasheetsCount, keywords);
        keywords.removeIf((k, count) -> count >= datasheetsCount);
    }
    @Override
    public Path outDir() {
        return factionDir;
    }

    @Override
    public Map<String, Object> asMap() {
        return Map.of(
                "name", name,
                "datasheets", datasheets,
                "keywords", keywords
        );
    }
}
