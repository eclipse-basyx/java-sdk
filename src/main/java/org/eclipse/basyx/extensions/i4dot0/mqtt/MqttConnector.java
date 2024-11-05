/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.i4dot0.mqtt;

import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIHelper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Set up the mqtt connection to a broker. It is also used to publish the mqtt messages. All information is provided by the {@link Databus}.
 *
 * @author wand
 */
public class MqttConnector implements CallbackExtender {

	private static final Logger logger = LoggerFactory.getLogger(MqttConnector.class);
	private MqttClient client;
	private String pubTopic;

	protected void connect(String serverUri, String subTopic, String pubTopic, String user, char[] pw) {
		this.pubTopic = pubTopic;
		try (MemoryPersistence persistence = new MemoryPersistence()) {
			client = new MqttClient(serverUri, UUID.randomUUID().toString(), persistence);
			if (user != null && pw != null) {
				MqttConnectOptions options = MqttAASAPIHelper.getMqttConnectOptions(user, pw);
				client.connect(options);
			} else {
				client.connect();
			}
			client.setCallback(this);
			client.subscribe(subTopic);
		} catch (MqttException e) {
			logger.error("MQTT connection couldn't be established - {}", e.getMessage());
		}
	}

	protected void publish(MqttMessage message) {
		try {
			client.publish(pubTopic, message);
		} catch (MqttException e) {
			logger.error("Couldn't publish message - {}", message.getId());
		}
	}

	protected MqttClient getClient() {
		return client;
	}
}
