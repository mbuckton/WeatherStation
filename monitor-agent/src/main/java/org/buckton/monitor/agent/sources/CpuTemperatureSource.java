package org.buckton.monitor.agent.sources;


import org.buckton.monitor.agent.reading.BaseReading;
import org.buckton.monitor.agent.reading.FloatReading;

import java.nio.file.Path;

public final class CpuTemperatureSource implements ValueSource<Double> {

  private static final Path PATH = Path.of("/sys/class/thermal/thermal_zone0/temp");

  private final String description;

  public CpuTemperatureSource() {
    this.description = "CPU temperature";
  }

  @Override
  public BaseReading<Double> getReading() {
    String raw = FileReaders.readFirstLine(PATH);
    if (raw == null) {
      return null;
    }
    try {
      long milliDegrees = Long.parseLong(raw.trim());
      double celsius = milliDegrees / 1000.0d;
      return new FloatReading("temperature", description, celsius);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }
}
