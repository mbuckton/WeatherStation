package org.buckton.monitor.agent.sources;


import org.buckton.monitor.agent.reading.BaseReading;
import org.buckton.monitor.agent.reading.ListReading;
import org.buckton.monitor.agent.reading.LongReading;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ThrottledSource implements ValueSource<List<String>>{

  private final String description;

  public ThrottledSource() {
    this.description = "Throttled state";
  }

  @Override
  public BaseReading<List<String>> getReading() {
    String line = execute("vcgencmd", "get_throttled");
    if (line == null) {
      return null;
    }

    int equalsIndex = line.indexOf('=');
    if (equalsIndex < 0) {
      return null;
    }

    String valuePart = line.substring(equalsIndex + 1).trim();
    if (valuePart.startsWith("0x") || valuePart.startsWith("0X")) {
      valuePart = valuePart.substring(2);
    }

    long mask;
    try {
      mask = Long.parseLong(valuePart, 16);
    } catch (NumberFormatException ignored) {
      return null;
    }

    List<String> states = decode(mask);
    return new ListReading("throttled", description, states);
  }

  private static List<String> decode(long mask) {
    List<String> states = new ArrayList<>();

    // Current state bits
    if ((mask & 0x1) != 0) {
      states.add("undervoltage_detected");
    }
    if ((mask & 0x2) != 0) {
      states.add("arm_frequency_capped");
    }
    if ((mask & 0x4) != 0) {
      states.add("throttling_active");
    }
    if ((mask & 0x8) != 0) {
      states.add("soft_temperature_limit_active");
    }

    // Historical bits
    if ((mask & 0x10000) != 0) {
      states.add("undervoltage_occurred_historically");
    }
    if ((mask & 0x20000) != 0) {
      states.add("arm_frequency_capped_historically");
    }
    if ((mask & 0x40000) != 0) {
      states.add("throttling_occurred_historically");
    }
    if ((mask & 0x80000) != 0) {
      states.add("soft_temperature_limit_occurred_historically");
    }

    return states;
  }

  private static String execute(String... command) {
    try {
      Process process = new ProcessBuilder(command)
          .redirectErrorStream(true)
          .start();

      try (BufferedReader reader =
               new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.US_ASCII))) {

        String line = reader.readLine();
        int exitCode = process.waitFor();
        if (exitCode != 0 || line == null) {
          return null;
        }

        String trimmed = line.trim();
        return trimmed.isEmpty() ? null : trimmed;
      }
    } catch (Exception ignored) {
      return null;
    }
  }
}
