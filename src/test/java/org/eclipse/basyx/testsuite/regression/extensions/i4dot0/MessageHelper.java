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

import org.eclipse.basyx.extensions.i4dot0.domain.MessageFrame;
import org.eclipse.basyx.extensions.i4dot0.domain.message.GenericMessage;
import org.eclipse.basyx.extensions.i4dot0.domain.message.SubmodelElementCollectionMessage;
import org.eclipse.basyx.extensions.i4dot0.domain.message.SubmodelMessage;
import org.eclipse.basyx.extensions.i4dot0.domain.message.frame.*;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;

import java.util.*;

/**
 * Helper class to generate an instance of each subclass of {@link org.eclipse.basyx.extensions.i4dot0.domain.Message}. Shows example usage for each message type, as well as the usage of the {@link MessageFrame}.
 *
 * @author wand
 */
public class MessageHelper {

	private final List<Submodel> submodelList;

	public MessageHelper(List<Submodel> submodelList) {
		this.submodelList = submodelList;
	}

	public GenericMessage generateGenericMessage() {
		GenericMessage genericMessage = new GenericMessage();
		genericMessage.setFrame(generateMessageFrame());

		Map<String, Object> interactionElements = new HashMap<>();
		interactionElements.put("interactionElement1", "Bob");
		interactionElements.put("interactionElement2", "Eve");
		genericMessage.setInteractionElements(List.of(interactionElements));

		return genericMessage;
	}

	public SubmodelMessage generateSubmodelMessage() {
		SubmodelMessage submodelMessage = new SubmodelMessage();
		submodelMessage.setFrame(generateMessageFrame());

		submodelMessage.setInteractionElements(List.of(submodelList.get(0)));
		return submodelMessage;
	}

	public SubmodelElementCollectionMessage generateSubmodelElementCollectionMessage() {
		SubmodelElementCollectionMessage smcMessage = new SubmodelElementCollectionMessage();
		smcMessage.setFrame(generateMessageFrame());
		smcMessage.setInteractionElements(List.of((SubmodelElementCollection) submodelList.get(1).getSubmodelElement("OperatingManual")));
		return smcMessage;
	}

	private MessageFrame generateMessageFrame() {
		MessageFrame frame = new MessageFrame();
		frame.setMessageId(0);
		frame.setConversationId(UUID.randomUUID().toString());

		Participant receiver = new Participant();
		ParticipantIdentification receiverIdentification = new ParticipantIdentification();
		ParticipantRole receiverRole = new ParticipantRole();

		receiverIdentification.setId("TEST_RECEIVER");
		receiverIdentification.setIdType("CUSTOM");
		receiverRole.setName("InformationReceiver");

		receiver.setIdentification(receiverIdentification);
		receiver.setRole(receiverRole);

		frame.setReceiver(receiver);

		Participant sender = new Participant();
		ParticipantIdentification senderIdentification = new ParticipantIdentification();
		ParticipantRole senderRole = new ParticipantRole();

		senderIdentification.setId("TEST_SENDER");
		senderIdentification.setIdType("CUSTOM");
		senderRole.setName("InformationSender");

		sender.setIdentification(senderIdentification);
		sender.setRole(senderRole);

		frame.setSender(sender);

		SemanticProtocol semanticProtocol = new SemanticProtocol();

		List<SemanticProtocolEntry> list = new ArrayList<>();
		SemanticProtocolEntry valuedIdentification = new SemanticProtocolEntry();

		valuedIdentification.setType("GLOBAL_REFERENCE");
		valuedIdentification.setIdType("CUSTOM");
		valuedIdentification.setValue("TEST_PROTOCOL");

		list.add(valuedIdentification);
		semanticProtocol.setKeys(list);

		frame.setSemanticProtocol(semanticProtocol);
		frame.setType("TEST_MESSAGE");

		return frame;
	}
}
