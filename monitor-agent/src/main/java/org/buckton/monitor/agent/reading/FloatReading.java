package org.buckton.monitor.agent.reading;

public final class FloatReading extends BaseReading<Double> {

  public FloatReading(String name, String description, Double value) {
    super(name, description, value);
  }
}