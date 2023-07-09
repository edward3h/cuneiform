package com.ordoacerbus.cuneiform;

import java.io.IOException;
import java.nio.file.Path;

public interface Processor {
    Metadata processInto(Path outdir) throws IOException;
}
