package org.buckton.monitor.agent;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.buckton.monitor.agent.config.AppConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public final class MqttJsonPublisher implements AutoCloseable {

  private final String brokerUrl;
  private final String topic;
  private final String clientId;
  private final String username;
  private final String password;

  private final MqttConnectOptions connectOptions;

  private MqttClient client;

  public MqttJsonPublisher(AppConfig.MqttConfig config) {
    Objects.requireNonNull(config, "config");

    this.brokerUrl = requireNonBlank(config.brokerUrl, "mqtt.brokerUrl");
    this.topic = requireNonBlank(config.topic, "mqtt.topic");
    this.clientId = requireNonBlank(config.clientId, "mqtt.clientId");
    this.username = config.username;
    this.password = config.password;

    this.connectOptions = buildOptions();
  }

  public void publish(String payload) {
    String messagePayload = requireNonBlank(payload, "payload");

    try {
      ensureConnected();

      MqttMessage message = new MqttMessage(messagePayload.getBytes(StandardCharsets.UTF_8));
      message.setQos(0);
      message.setRetained(false);

      client.publish(topic, message);
    } catch (MqttException exception) {
      disconnectQuietly();
      throw new RuntimeException("MQTT publish failed", exception);
    }
  }

  private void ensureConnected() throws MqttException {
    if (client == null) {
      client = new MqttClient(brokerUrl, clientId);
    }
    if (client.isConnected()) {
      return;
    }

    try {
      client.connect(connectOptions);
    } catch (MqttException exception) {
      closeClientQuietly();
      client = null;
      throw exception;
    }
  }

  private MqttConnectOptions buildOptions() {
    MqttConnectOptions options = new MqttConnectOptions();
    options.setCleanSession(true);
    options.setAutomaticReconnect(false);
    options.setConnectionTimeout(10);
    options.setKeepAliveInterval(30);

    if (username != null && !username.isBlank()) {
      options.setUserName(username);
    }
    if (password != null && !password.isBlank()) {
      options.setPassword(password.toCharArray());
    }

    return options;
  }

  private void disconnectQuietly() {
    if (client == null) {
      return;
    }
    try {
      if (client.isConnected()) {
        client.disconnect();
      }
    } catch (MqttException ignored) {
      // Nothing productive to do here.
    }
  }

  private void closeClientQuietly() {
    if (client == null) {
      return;
    }
    try {
      client.close();
    } catch (MqttException ignored) {
      // Nothing productive to do here.
    }
  }

  @Override
  public void close() {
    disconnectQuietly();
    closeClientQuietly();
    client = null;
  }

  private static String requireNonBlank(String value, String name) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Missing required config: " + name);
    }
    return value.trim();
  }
}
