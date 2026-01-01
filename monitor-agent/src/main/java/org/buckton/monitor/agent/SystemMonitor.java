package org.buckton.monitor.agent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.buckton.monitor.agent.config.AppConfig;
import org.buckton.monitor.agent.config.LoadConfig;
import org.buckton.monitor.agent.reading.BaseReading;
import org.buckton.monitor.agent.sources.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemMonitor {

  private final List<ValueSource<?>> sources;
  private final ScheduledExecutorService scheduler;
  private final AppConfig appConfig;

  public SystemMonitor(AppConfig appConfig){
    this.appConfig = appConfig;
    sources = new ArrayList<>();
    sources.add(new CpuTemperatureSource());
    sources.add(new Load1Source());
    sources.add(new MemAvailableSource());
    sources.add(new ThrottledSource());
    sources.add(new UptimeSource());
    this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
      Thread thread = new Thread(runnable, "SystemMonitor");
      thread.setDaemon(true);
      return thread;
    });
    scheduler.scheduleAtFixedRate(() -> {
      try {
        publishStats();
      } catch (Exception e) {
        System.err.println("SystemMonitor run failed: " + e.getMessage());
        e.printStackTrace(System.err);
      }
    }, 0L, appConfig.interval, TimeUnit.SECONDS);
  }

  public void publishStats(){
    MqttJsonPublisher publisher = new MqttJsonPublisher(appConfig.getMqtt());
    JsonObject data = collect();
    publisher.publish(data.toString());
  }

  public JsonObject collect() {
    JsonObject out = new JsonObject();
    for (ValueSource<?> source : sources) {
      BaseReading<?> reading = source.getReading();
      if (reading == null) {
        continue;
      }
      addValue(out, reading.getName(), reading.getValue());
    }
    return out;
  }


  private void addValue(JsonObject out, String name, Object value) {
    if (value instanceof Number number) {
      out.addProperty(name, number);
      return;
    }
    if (value instanceof Boolean bool) {
      out.addProperty(name, bool);
      return;
    }
    if (value instanceof Iterable<?> iterable) {
      JsonArray array = new JsonArray();
      for (Object item : iterable) {
        if (item == null) {
          continue;
        }
        if (item instanceof Number n) {
          array.add(n);
          continue;
        }
        if (item instanceof Boolean b) {
          array.add(b);
          continue;
        }
        array.add(String.valueOf(item));
      }
      out.add(name, array);
      return;
    }
    out.addProperty(name, String.valueOf(value));
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    AppConfig appConfig = LoadConfig.loadConfig(args);
    SystemMonitor monitor = new SystemMonitor(appConfig);
    Thread.currentThread().join();
  }
}
