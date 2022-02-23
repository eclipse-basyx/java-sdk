/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.basyx.extensions.aas.api.mqtt;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.basyx.aas.restapi.observing.IAASAPIObserver;
import org.eclipse.basyx.aas.restapi.observing.ObservableAASAPI;
import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link IAASAPIObserver} Triggers MQTT events for different
 * operations on the AAS.
 * 
 * @author fried
 *
 */
public class MqttAASAPIObserver extends MqttEventService implements IAASAPIObserver {
	private static Logger logger = LoggerFactory.getLogger(MqttAASAPIObserver.class);

	// The underlying AASAPI
	protected ObservableAASAPI observedAPI;

	// Submodel whitelist for filtering
	protected boolean useWhitelist = false;
	protected Set<String> whitelist = new HashSet<>();

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String serverEndpoint, String clientId) throws MqttException {
		this(observedAPI, serverEndpoint, clientId, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI with a
	 * custom persistence strategy
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String brokerEndpoint, String clientId, MqttClientPersistence persistence) throws MqttException {
		super(brokerEndpoint, clientId, persistence);
		logger.info("Create new MQTT AASAPI for endpoint " + brokerEndpoint);
		this.observedAPI = observedAPI;
		observedAPI.addObserver(this);
		sendMqttMessage(MqttAASAPIHelper.TOPIC_CREATEAPI, observedAPI.getAAS().getIdentification().getId());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		this(observedAPI, serverEndpoint, clientId, user, pw, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI with
	 * credentials and persistency strategy
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence persistence) throws MqttException {
		super(serverEndpoint, clientId, user, pw);
		logger.info("Create new MQTT AASAPI for endpoint " + serverEndpoint);
		this.observedAPI = observedAPI;
		observedAPI.addObserver(this);
		sendMqttMessage(MqttAASAPIHelper.TOPIC_CREATEAPI, observedAPI.getAAS().getIdentification().getId());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI.
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @param client
	 *            An already connected mqtt client
	 * @throws MqttException
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, MqttClient client) throws MqttException {
		super(client);
		this.observedAPI = observedAPI;
		observedAPI.addObserver(this);
		sendMqttMessage(MqttAASAPIHelper.TOPIC_CREATEAPI, observedAPI.getAAS().getIdentification().getId());
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
	 * Disables the submodel filter whitelist
	 * 
	 */
	public void disableWhitelist() {
		useWhitelist = false;
	}

	/**
	 * Enables the submodel filter whitelist
	 * 
	 */
	public void enableWhitelist() {
		useWhitelist = true;
	}

	@Override
	public void submodelAdded(IReference submodel) {
		for (IKey key : submodel.getKeys()) {
			String id = key.getValue();
			if (filter(id)) {
				sendMqttMessage(MqttAASAPIHelper.TOPIC_ADDSUBMODEL, getCombinedMessage(observedAPI.getAAS().getIdShort(), id));
			}
		}
	}

	@Override
	public void submodelRemoved(String id) {
		if (filter(id)) {
			sendMqttMessage(MqttAASAPIHelper.TOPIC_REMOVESUBMODEL, getCombinedMessage(observedAPI.getAAS().getIdShort(), id));
		}
	}

	public static String getCombinedMessage(String aasId, String idShort) {
		return "(" + aasId + "," + idShort + ")";
	}

	private boolean filter(String idShort) {
		idShort = VABPathTools.stripSlashes(idShort);
		return !useWhitelist || whitelist.contains(idShort);
	}
}
