package org.buckton.monitor.agent.reading;


import java.util.List;

public final class ListReading<T> extends BaseReading<List<T>> {

  public ListReading(String name, String description, List<T> value) {
    super(name, description, value);
  }
}

