package org.buckton.monitor.agent.config;

import org.buckton.monitor.agent.SystemMonitor;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoadConfig {

  public static AppConfig loadConfig(String[] args) throws IOException {
    Yaml yaml = new Yaml();
    AppConfig config;
    if (args.length > 0 && args[0] != null && !args[0].isBlank()) {
      Path configPath = Path.of(args[0]);
      try (InputStream in = Files.newInputStream(configPath)) {
        config = yaml.loadAs(in, AppConfig.class);
      }
    } else {
      try (InputStream in = SystemMonitor.class
          .getClassLoader()
          .getResourceAsStream("app.yaml")) {

        if (in == null) {
          throw new IllegalStateException("app.yaml not found on classpath");
        }

        config = yaml.loadAs(in, AppConfig.class);
      }
    }
    return config;
  }
}
