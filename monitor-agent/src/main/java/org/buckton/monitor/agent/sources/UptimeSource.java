package org.buckton.monitor.agent.sources;

import org.buckton.monitor.agent.reading.BaseReading;
import org.buckton.monitor.agent.reading.DurationReading;

import java.nio.file.Path;

public final class UptimeSource implements ValueSource<Long> {

  private static final Path PATH = Path.of("/proc/uptime");

  private final String description;

  public UptimeSource() {
    this.description = "Uptime of server";
  }

  @Override
  public BaseReading<Long> getReading() {
    String line = FileReaders.readFirstLine(PATH);
    if (line == null) {
      return null;
    }
    int spaceIndex = line.indexOf(' ');
    String firstToken = spaceIndex < 0 ? line : line.substring(0, spaceIndex);
    try {
      double seconds = Double.parseDouble(firstToken.trim());
      long millis = (long) (seconds * 1000.0d);
      return new DurationReading("uptime", description, millis);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }
}
