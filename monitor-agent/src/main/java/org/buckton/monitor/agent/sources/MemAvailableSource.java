package org.buckton.monitor.agent.sources;


import org.buckton.monitor.agent.reading.BaseReading;
import org.buckton.monitor.agent.reading.LongReading;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class MemAvailableSource implements ValueSource<Long> {

  private static final Path PATH = Path.of("/proc/meminfo");

  private final String description;

  public MemAvailableSource() {
    this.description = "Memory available";
  }

  @Override
  public BaseReading<Long> getReading() {
    if (!Files.exists(PATH)) {
      return null;
    }
    List<String> lines;
    try {
      lines = Files.readAllLines(PATH, StandardCharsets.US_ASCII);
    } catch (IOException ignored) {
      return null;
    }

    for (String line : lines) {
      if (line == null) {
        continue;
      }
      String trimmed = line.trim();
      if (!trimmed.startsWith("MemAvailable:")) {
        continue;
      }
      // Example: "MemAvailable:   123456 kB"
      String[] parts = trimmed.split("\\s+");
      if (parts.length < 2) {
        return null;
      }
      try {
        long kiloBytes = Long.parseLong(parts[1].trim());
        long bytes = kiloBytes * 1024L;
        return new LongReading("memAvailable", description, bytes);
      } catch (NumberFormatException ignored) {
        return null;
      }
    }

    return null;
  }
}
