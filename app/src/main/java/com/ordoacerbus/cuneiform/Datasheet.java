package com.ordoacerbus.cuneiform;

import io.soabase.recordbuilder.core.RecordBuilderFull;

import java.util.List;
@RecordBuilderFull
public record Datasheet(String title, Page pageFront, Page pageBack, List<Keyword> keywords, List<String> faction) {
}
