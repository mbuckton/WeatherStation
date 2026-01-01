package org.buckton.monitor.agent.reading;

public final class DurationReading extends BaseReading<Long> {

  public DurationReading(String name, String description, long durationMillis) {
    super(name, description, durationMillis);
  }

}
