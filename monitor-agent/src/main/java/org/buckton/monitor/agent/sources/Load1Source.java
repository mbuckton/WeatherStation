package org.buckton.monitor.agent.sources;


import org.buckton.monitor.agent.reading.BaseReading;
import org.buckton.monitor.agent.reading.FloatReading;

import java.nio.file.Path;

public final class Load1Source implements ValueSource<Double> {

  private static final Path PATH = Path.of("/proc/loadavg");

  private final String description;

  public Load1Source() {
    this.description = "1 Minute load average";
  }

  @Override
  public BaseReading<Double> getReading() {
    String line = FileReaders.readFirstLine(PATH);
    if (line == null) {
      return null;
    }
    int spaceIndex = line.indexOf(' ');
    String firstToken = spaceIndex < 0 ? line : line.substring(0, spaceIndex);
    try {
      double load1 = Double.parseDouble(firstToken.trim());
      return new FloatReading("load1", description, load1);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }
}