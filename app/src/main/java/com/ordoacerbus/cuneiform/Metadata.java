package com.ordoacerbus.cuneiform;

import java.nio.file.Path;
import java.util.Map;

public interface Metadata {
    Path outDir();

    Map<String, Object> asMap();
}
