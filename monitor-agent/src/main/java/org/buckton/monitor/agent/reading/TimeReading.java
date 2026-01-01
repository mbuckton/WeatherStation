package org.buckton.monitor.agent.reading;


import java.time.Instant;

public final class TimeReading extends BaseReading<Instant> {

  public TimeReading(String name, String description, Instant value) {
    super(name, description, value);
  }
}
