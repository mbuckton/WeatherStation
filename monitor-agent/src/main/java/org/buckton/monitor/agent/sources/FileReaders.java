package org.buckton.monitor.agent.sources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileReaders {

  private FileReaders() {
  }

  public static String readFirstLine(Path path) {
    try {
      if (!Files.exists(path)) {
        return null;
      }
      String content = Files.readString(path, StandardCharsets.US_ASCII);
      if (content == null) {
        return null;
      }
      String trimmed = content.trim();
      if (trimmed.isEmpty()) {
        return null;
      }
      int newlineIndex = trimmed.indexOf('\n');
      if (newlineIndex > 0) {
        return trimmed.substring(0, newlineIndex).trim();
      }
      return trimmed;
    } catch (IOException ignored) {
      return null;
    }
  }
}