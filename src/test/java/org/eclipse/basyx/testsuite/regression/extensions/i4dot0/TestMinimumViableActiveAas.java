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
package org.eclipse.basyx.testsuite.regression.extensions.i4dot0;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import org.eclipse.basyx.aas.factory.json.JSONToMetamodelConverter;
import org.eclipse.basyx.extensions.i4dot0.mqtt.Databus;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.testsuite.regression.extensions.i4dot0.callback.GenericMqttCallback;
import org.eclipse.basyx.testsuite.regression.extensions.i4dot0.callback.SmcMqttCallback;
import org.eclipse.basyx.testsuite.regression.extensions.i4dot0.callback.SubmodelMqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test of the I4.0-Extension. Represents minimum viable setup for each {@link org.eclipse.basyx.extensions.i4dot0.domain.Message} type. Shows example message handling and validates the received data.
 *
 * @author wand
 */
public class TestMinimumViableActiveAas {

	private static Server mqttBroker;
	private static GenericMqttCallback genericMqttCallback;
	private static SubmodelMqttCallback submodelMqttCallback;
	private static SmcMqttCallback smcMqttCallback;
	private static List<Submodel> submodelList;

	@BeforeClass
	public static void setup() throws IOException, InterruptedException {
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);

		String jsonPath = "src/test/resources/aas/factory/json/aasJsonSchemaV2.0.1_Example.json";
		String json = new String(Files.readAllBytes(Paths.get(jsonPath)));
		JSONToMetamodelConverter converter = new JSONToMetamodelConverter(json);
		submodelList = converter.parseSubmodels();

		Databus genericDatabus = new Databus();
		MqttClient genericMqttClient = genericDatabus.init("tcp://localhost:1884", "basyx_test/generic_topic", "basyx_test/generic_topic", null, null);
		genericMqttCallback = new GenericMqttCallback();
		genericMqttClient.setCallback(genericMqttCallback);

		Databus submodelDatabus = new Databus();
		MqttClient submodelMqttClient = submodelDatabus.init("tcp://localhost:1884", "basyx_test/submodel_topic", "basyx_test/submodel_topic", null, null);
		submodelMqttCallback = new SubmodelMqttCallback();
		submodelMqttClient.setCallback(submodelMqttCallback);

		Databus smcDatabus = new Databus();
		MqttClient smcMqttClient = smcDatabus.init("tcp://localhost:1884", "basyx_test/smc_topic", "basyx_test/smc_topic", null, null);
		smcMqttCallback = new SmcMqttCallback();
		smcMqttClient.setCallback(smcMqttCallback);

		MessageHelper messageHelper = new MessageHelper(submodelList);
		genericDatabus.publish(messageHelper.generateGenericMessage());
		submodelDatabus.publish(messageHelper.generateSubmodelMessage());
		smcDatabus.publish(messageHelper.generateSubmodelElementCollectionMessage());

		//this sleep ensures that all messages are received
		Thread.sleep(500);
	}

	@AfterClass
	public static void stopMqttBroker() {
		if (mqttBroker == null) {
			return;
		}
		mqttBroker.stopServer();
	}

	@Test
	public void checkMessages() {
		Map<String, Object> interactionElements = new HashMap<>();
		interactionElements.put("interactionElement1", "Bob");
		interactionElements.put("interactionElement2", "Eve");
		assertEquals(genericMqttCallback.getMostRecentMessage().getInteractionElements(), List.of(interactionElements));

		Submodel receivedSubmodel = submodelMqttCallback.getMostRecentMessage().getInteractionElements().get(0);
		submodelList.get(0).forEach((key, value) -> assertEquals(receivedSubmodel.get(key), value));

		SubmodelElementCollection receivedSmc = smcMqttCallback.getMostRecentMessage().getInteractionElements().get(0);
		((SubmodelElementCollection) submodelList.get(1).getSubmodelElement("OperatingManual")).forEach((key, value) -> assertEquals(receivedSmc.get(key), value));
	}
}
