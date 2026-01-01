package org.buckton.monitor.agent.reading;


public final class BooleanReading extends BaseReading<Boolean> {

  public BooleanReading(String name, String description, Boolean value) {
    super(name, description, value);
  }
}