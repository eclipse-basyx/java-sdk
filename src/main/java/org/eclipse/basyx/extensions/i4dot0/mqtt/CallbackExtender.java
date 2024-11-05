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

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends the default {@link MqttCallback} to provide debug logs for each callback method.
 * <p>
 * Implement this interface to handle incoming messages.
 *
 * @author wand
 */
public interface CallbackExtender extends MqttCallback {

	Logger logger = LoggerFactory.getLogger(MqttConnector.class);

	default void connectionLost(Throwable throwable) {
		logger.error("MQTT lost connection: {}", Databus.getUri());
	}

	default void messageArrived(String s, MqttMessage mqttMessage) {
		logger.debug("Received MQTT message | Payload - Endpoint | {} - {}", mqttMessage.getPayload(), Databus.getUri());
	}

	default void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		logger.debug("Successfully delivered message | MessageId - Endpoint | {} - {}", iMqttDeliveryToken.getMessageId(), Databus.getUri());
	}
}
