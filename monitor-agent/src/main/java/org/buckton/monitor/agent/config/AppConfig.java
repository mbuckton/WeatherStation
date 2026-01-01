package org.buckton.monitor.agent.config;

import lombok.Data;

@Data
public class AppConfig {

  public MqttConfig mqtt;
  public int interval;

  public static class MqttConfig {
    public String brokerUrl;
    public String clientId;
    public String topic;
    public String username;
    public String password;
  }
}
