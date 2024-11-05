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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.basyx.extensions.i4dot0.domain.Message;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to set up a mqtt connection to a broker and publish I4.0-Language messages. Those messages need to be built as instance of {@link Message}.
 *
 * @author wand
 */
public class Databus {

	private static final Logger logger = LoggerFactory.getLogger(Databus.class);
	private static String uri;
	private MqttConnector mqttConnector;

	/**
	 * Gets the connection uri
	 *
	 * @return The URI of the MQTT broker connector to
	 */
	public static String getUri() {
		return uri;
	}

	/**
	 * Sets the connection uri
	 *
	 * @param uri
	 * 		URI of the MQTT broker.
	 */
	private static void setUri(String uri) {
		Databus.uri = uri;
	}

	/**
	 * Provide the following parameter to set up a mqtt connection to a broker. This client is set up specifically to handle I4.0 communication.
	 *
	 * @param serverUri
	 * 		The URI of the MQTT broker.
	 * @param subTopic
	 * 		The topic from which the messages are received.
	 * @param pubTopic
	 * 		The topic on which messages are published.
	 * @param user
	 * 		Pass the username of the mqtt broker if it uses authentication, otherwise null
	 * @param pw
	 * 		Pass the password of the mqtt broker if it uses authentication, otherwise null
	 * @return MqttClient to be able to override the callback if needed.
	 */
	public MqttClient init(String serverUri, String subTopic, String pubTopic, String user, char[] pw) {
		setUri(serverUri);
		mqttConnector = new MqttConnector();
		mqttConnector.connect(serverUri, subTopic, pubTopic, user, pw);
		return mqttConnector.getClient();
	}

	/**
	 * Use this method to publish I4.0 messages.
	 *
	 * @param message
	 * 		The message to be published. Should be an extension of the class {@link Message}.
	 */
	public void publish(Message message) {
		MqttMessage mqttMessage = new MqttMessage();
		ObjectMapper mapper = new ObjectMapper();
		mqttMessage.setQos(1);
		mqttMessage.setRetained(false);
		try {
			mqttMessage.setPayload(mapper.writeValueAsBytes(message));
		} catch (JsonProcessingException e) {
			logger.error("Unable to serialize I4.0 message!");
		}
		mqttConnector.publish(mqttMessage);
	}
}
