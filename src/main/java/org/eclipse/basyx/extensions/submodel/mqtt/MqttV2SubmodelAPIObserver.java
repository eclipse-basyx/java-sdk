/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.extensions.submodel.mqtt;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttV2SubmodelAggregatorHelper;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.restapi.observing.ISubmodelAPIObserverV2;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ISubmodelAPIObserverV2} Triggers MQTT events for
 * different CRUD operations on the submodel.
 * 
 * @author conradi, danish, siebert
 *
 */
public class MqttV2SubmodelAPIObserver extends MqttEventService implements ISubmodelAPIObserverV2 {
	private static Logger logger = LoggerFactory.getLogger(MqttV2SubmodelAPIObserver.class);

	// Submodel Element whitelist for filtering
	protected boolean useWhitelist = false;
	protected Set<String> whitelist = new HashSet<>();
	
	private IIdentifier aasIdentifier;
	private IIdentifier submodelIdentifier;

	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI
	 * 
	 * @param client
	 *            An already connected mqtt client
	 * @param aasId 
	 * @param submodelIdentifier
	 * 
	 * @throws MqttException
	 */
	public MqttV2SubmodelAPIObserver(MqttClient client, IIdentifier aasId, IIdentifier submodelIdentifier, String repoId) throws MqttException {
		super(client);
		
		connectMqttClientIfRequired();
		
		this.aasIdentifier = aasId;
		this.submodelIdentifier = submodelIdentifier;
		
		sendMqttMessage(MqttV2SubmodelAggregatorHelper.createCreateSubmodelTopic(aasId.getId(), repoId), this.submodelIdentifier.getId());
	}
	
	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI
	 * 
	 * @param client
	 *            An already connected mqtt client
	 * @param aasId 
	 * @param submodelIdentifier
	 * @param options
	 * 
	 * @throws MqttException
	 */
	public MqttV2SubmodelAPIObserver(MqttClient client, IIdentifier aasId, IIdentifier submodelIdentifier, MqttConnectOptions options, String repoId) throws MqttException {
		super(client);
		
		connectMqttClientIfRequired(options);
		
		this.aasIdentifier = aasId;
		this.submodelIdentifier = submodelIdentifier;
		
		sendMqttMessage(MqttV2SubmodelAggregatorHelper.createCreateSubmodelTopic(aasId.getId(), repoId), this.submodelIdentifier.getId());
	}
		
	private void connectMqttClientIfRequired() throws MqttException {
		if(!mqttClient.isConnected()) {
			mqttClient.connect();
		}
	}
	
	private void connectMqttClientIfRequired(MqttConnectOptions options) throws MqttException {
		if(!mqttClient.isConnected()) {
			mqttClient.connect(options);
		}
	}

	/**
	 * Adds a submodel element to the filter whitelist. Can also be a path for
	 * nested submodel elements.
	 * 
	 * @param shortId
	 */
	public void observeSubmodelElement(String shortId) {
		whitelist.add(VABPathTools.stripSlashes(shortId));
	}

	/**
	 * Sets a new filter whitelist.
	 * 
	 * @param shortIds
	 */
	public void setWhitelist(Set<String> shortIds) {
		this.whitelist.clear();
		for (String entry : shortIds) {
			this.whitelist.add(VABPathTools.stripSlashes(entry));
		}
	}

	/**
	 * Disables the submodel element filter whitelist
	 * 
	 */
	public void disableWhitelist() {
		useWhitelist = false;
	}

	/**
	 * Enables the submodel element filter whitelist
	 * 
	 */
	public void enableWhitelist() {
		useWhitelist = true;
	}

	@Override
	public void elementAdded(String idShortPath, Object newValue, String aasId, String submodelId, String repoId) {	
		if (newValue instanceof Map<?, ?> && filter(idShortPath)) {
			ISubmodelElement submodelElement = setValueNull(newValue);
			sendMqttMessage(MqttV2SubmodelAPIHelper.createCreateSubmodelElementTopic(aasId, submodelId, idShortPath, repoId), serializePayload(submodelElement));
		}
	}

	@Override
	public void elementDeleted(String idShortPath, ISubmodelElement submodelElement, String aasId, String submodelId, String repoId) {
		if (submodelElement instanceof Map<?, ?> && filter(idShortPath)) {
			ISubmodelElement sme = setValueNull(submodelElement);
			sendMqttMessage(MqttV2SubmodelAPIHelper.createDeleteSubmodelElementTopic(aasId, submodelId, idShortPath, repoId), serializePayload(sme));
		}
	}

	@Override
	public void elementUpdated(String idShortPath, ISubmodelElement submodelElement, String aasId, String submodelId, String repoId) {
		if (submodelElement instanceof Map<?, ?> && filter(idShortPath)) {
			ISubmodelElement sme = setValueNull(submodelElement);
			sendMqttMessage(MqttV2SubmodelAPIHelper.createUpdateSubmodelElementTopic(aasId, submodelId, idShortPath, repoId), serializePayload(sme));
		}
	}
	
	@Override
	public void elementValue(String idShortPath, Object value, String aasId, String submodelId, String repoId) {
		if (filter(idShortPath)) {
			sendMqttMessage(MqttV2SubmodelAPIHelper.createSubmodelElementValueTopic(aasId, submodelId, idShortPath, repoId), serializePayload(value));			
		}
	}

	public static String getCombinedMessage(String aasId, String submodelId, String elementPart) {
		elementPart = VABPathTools.stripSlashes(elementPart);
		return "(" + aasId + "," + submodelId + "," + elementPart + ")";
	}

	private boolean filter(String idShort) {
		idShort = VABPathTools.stripSlashes(idShort);
		return !useWhitelist || whitelist.contains(idShort);
	}
	
	@SuppressWarnings("unchecked")
	private ISubmodelElement setValueNull(Object submodelElement) {
		Map<String, Object> map = SubmodelElementMapCollectionConverter.smElementToMap((Map<String, Object>) submodelElement);	
		Map<String, Object> copy = new LinkedHashMap<>(map);
		ISubmodelElement newSubmodelElement = SubmodelElementFacadeFactory.createSubmodelElement(copy);
		newSubmodelElement.setValue(null);
		
		return newSubmodelElement;
	}

	private String serializePayload(Object payload) {
		GSONTools tools = new GSONTools(new DefaultTypeFactory(), false, false);
		
		return tools.serialize(payload);
	}
	
}