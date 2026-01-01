package org.buckton.monitor.agent.sources;

import org.buckton.monitor.agent.reading.BaseReading;

public interface ValueSource<T> {

  BaseReading<T> getReading();
}
