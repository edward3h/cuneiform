package com.ordoacerbus.cuneiform;

import io.soabase.recordbuilder.core.RecordBuilderFull;

@RecordBuilderFull
public record Page(int number, PageType type, String title) {
    public String imagePath() {
        if (title == null) {
            return "#"; // TODO
        }
        return Util.safeName(title) + "_" + number + ".png";
    }
}
