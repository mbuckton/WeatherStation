package org.buckton.monitor.agent.reading;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BaseReading<T> {

  String name;

  String description;

  T value;
}
