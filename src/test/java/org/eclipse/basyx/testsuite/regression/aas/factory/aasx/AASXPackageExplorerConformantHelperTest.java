/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.factory.aasx;

import static org.junit.Assert.assertEquals;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.basyx.aas.factory.aasx.AASXFactory;
import org.eclipse.basyx.aas.factory.aasx.AASXPackageExplorerConformantHelper;
import org.eclipse.basyx.aas.factory.aasx.InMemoryFile;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AasEnv;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Test;

/**
 * 
 * @author fried
 *
 */
public class AASXPackageExplorerConformantHelperTest {
	@Test
	public void testRemoveAASRefInSubmodelRef() {
		Key submodelKey = createDummySubmodelKey();
		AssetAdministrationShell testAAS = createAASWithSingleSubmodelReferenceContainingAASKey(submodelKey);

		AasEnv env = AASXPackageExplorerConformantHelper.adapt(Collections.singleton(testAAS), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList());

		assertAASKeyRemovedFromSubmodelReferences(submodelKey, env);
	}

	private Key createDummySubmodelKey() {
		return new Key(KeyElements.SUBMODEL, false, "testSubmodel", IdentifierType.CUSTOM);
	}

	private void assertAASKeyRemovedFromSubmodelReferences(Key submodelKey, AasEnv env) {
		Collection<IAssetAdministrationShell> convertedAASs = env.getAssetAdministrationShells();
		assertEquals(1, convertedAASs.size());

		IAssetAdministrationShell convertedAAS = convertedAASs.iterator().next();
		Collection<IReference> convertedReferences = convertedAAS.getSubmodelReferences();
		assertEquals(1, convertedReferences.size());

		Reference expected = new Reference(submodelKey);
		assertEquals(expected, convertedReferences.iterator().next());
	}

	private AssetAdministrationShell createAASWithSingleSubmodelReferenceContainingAASKey(Key submodelKey) {
		AssetAdministrationShell testAAS = createDummyAAS("testAAS");

		Reference submodelReference = createSubmodelReference(testAAS, submodelKey);

		testAAS.addSubmodelReference(submodelReference);
		return testAAS;
	}

	private Reference createSubmodelReference(IAssetAdministrationShell aas, Key submodelKey) {
		Key aasKey = createDummyAASKey(aas);

		List<IKey> keys = Arrays.asList(aasKey, submodelKey);

		return new Reference(keys);
	}

	private AssetAdministrationShell createDummyAAS(final String aasId) {
		return new AssetAdministrationShell("testAASIdShort", new CustomId(aasId),
				new Asset("testAssetIdShort", new CustomId("testAsset"), AssetKind.INSTANCE));
	}

	private Key createDummyAASKey(IAssetAdministrationShell aas) {
		IIdentifier identifier = aas.getIdentification();
		return new Key(KeyElements.ASSETADMINISTRATIONSHELL, false, identifier.getId(), identifier.getIdType());
	}
	
	@Test
	public void checkForCrashWhenMultipleCopiesOfAASXFileIsGenerated() throws IOException, TransformerException, ParserConfigurationException {
		String aasIdShort = "IIP_Ecosphere";
		String rawURN = "urn:::AAS:::iipEcosphere#";
		
		AssetAdministrationShell assetAdministrationShell = createAssetAdministrationShell(aasIdShort, rawURN);
        
		String subModelIdShort = "platform";
		
		Submodel submodel = new Submodel(subModelIdShort, new CustomId(subModelIdShort));
		
		assetAdministrationShell.addSubmodel(submodel);

        List<IAssetAdministrationShell> listOfAssetAdministrationShell = Arrays.asList(assetAdministrationShell);
        List<ISubmodel> listOfSubmodels = Arrays.asList(submodel);
        
        Collection<IAsset> listOfAssets = new ArrayList<IAsset>();
        Collection<IConceptDescription> listOfConceptDescription = new ArrayList<IConceptDescription>();
        
        int noOfFilesToGenerate = 7;
        
        String filePath = "src\\test\\resources\\TestPackage.aasx";
        
        for (int i = 0; i < noOfFilesToGenerate; i++) {
            FileOutputStream aasxOutputFile = new FileOutputStream(filePath);
            
            AASXPackageExplorerConformantHelper.adapt(listOfAssetAdministrationShell, listOfAssets, listOfConceptDescription, listOfSubmodels);
            
            AASXFactory.buildAASX(listOfAssetAdministrationShell, listOfAssets, listOfConceptDescription, listOfSubmodels, 
            		new ArrayList<InMemoryFile>(), aasxOutputFile);
            
            aasxOutputFile.close();
        }
	}

	private AssetAdministrationShell createAssetAdministrationShell(String idShort, String modelURN) {
		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell();
		
        assetAdministrationShell.setIdShort(idShort);
        assetAdministrationShell.setIdentification(new ModelUrn(modelURN));
        
		return assetAdministrationShell;
	}
}
