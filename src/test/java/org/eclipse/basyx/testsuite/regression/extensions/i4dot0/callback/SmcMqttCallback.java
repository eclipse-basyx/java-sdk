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
package org.eclipse.basyx.testsuite.regression.extensions.i4dot0.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.basyx.extensions.i4dot0.domain.message.SubmodelElementCollectionMessage;
import org.eclipse.basyx.extensions.i4dot0.mqtt.CallbackExtender;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;

/**
 * Callback to handle an incoming {@link SubmodelElementCollectionMessage}.
 *
 * @author wand
 */
public class SmcMqttCallback implements CallbackExtender {

	private SubmodelElementCollectionMessage message;

	@Override
	public void messageArrived(String s, MqttMessage mqttMessage) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			message = mapper.readValue(mqttMessage.getPayload(), SubmodelElementCollectionMessage.class);
		} catch (IOException e) {
			logger.error("Unable to parse SmcMqttCallback");
		}
	}

	public SubmodelElementCollectionMessage getMostRecentMessage() {
		return message;
	}
}
