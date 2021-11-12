/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.testsuite.regression.registry.descriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.basyx.registry.descriptor.ModelDescriptor;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.junit.Test;

/**
 * Test suite for Model Descriptor common method testing
 *
 * @author haque, fischer, fried
 *
 */
public abstract class ModelDescriptorTestSuite {
	private static final String TESTENDPOINT = "dummy.com";
	private static final String TESTENDPOINT2 = "dummy2.com";
	private static final String TEST_ADMINISTRATION_VERSION = "v0";
	private static final String TEST_ADMINISTRATION_REVISION = "a";

	private ModelDescriptor descriptor;

	public abstract ModelDescriptor retrieveModelDescriptor();

	@Test
	public void testAddEndpoint() {
		addEndpoints();
		Collection<Endpoint> endpoints = descriptor.getEndpoints();
		assertTrue(endpoints.stream().anyMatch(x -> x.getProtocolInformation().getEndpointAddress().equals(TESTENDPOINT)));
		assertTrue(endpoints.stream().anyMatch(x -> x.getProtocolInformation().getEndpointAddress().equals(TESTENDPOINT2)));
	}

	@Test
	public void testRemoveEndpoint() {
		addEndpoints();
		descriptor.removeEndpoint(TESTENDPOINT);
		Collection<Endpoint> endpoints = descriptor.getEndpoints();
		assertTrue(!endpoints.stream().anyMatch(x -> x.getProtocolInformation().getEndpointAddress().equals(TESTENDPOINT)));
		assertTrue(endpoints.stream().anyMatch(x -> x.getProtocolInformation().getEndpointAddress().equals(TESTENDPOINT2)));
	}

	@Test
	public void setAdministration() {
		descriptor = retrieveModelDescriptor();
		descriptor.setAdministration(new AdministrativeInformation(TEST_ADMINISTRATION_VERSION, TEST_ADMINISTRATION_REVISION));
		AdministrativeInformation adminInformation = descriptor.getAdministration();
		assertEquals(TEST_ADMINISTRATION_VERSION, adminInformation.getVersion());
		assertEquals(TEST_ADMINISTRATION_REVISION, adminInformation.getRevision());
		assertEquals("", adminInformation.getRevision());
		assertEquals("", adminInformation.getVersion());
	}

	private void addEndpoints() {
		descriptor = retrieveModelDescriptor();
		descriptor.addEndpoint(new Endpoint(TESTENDPOINT));
		descriptor.addEndpoint(new Endpoint(TESTENDPOINT2));
	}
}
