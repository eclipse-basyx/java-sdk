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
package org.eclipse.basyx.extensions.i4dot0.domain;

import org.eclipse.basyx.extensions.i4dot0.domain.message.frame.Participant;
import org.eclipse.basyx.extensions.i4dot0.domain.message.frame.SemanticProtocol;

import javax.validation.constraints.NotNull;

/**
 * Represents the frame of an I4.0 language message. All NotNull member variables are mandatory to create a valid I4.0 message.
 *
 * @author wand
 */

public class MessageFrame {

	@NotNull
	private String type;
	private Participant sender;
	private Participant receiver;
	@NotNull
	private String conversationId;
	@NotNull
	private Object messageId;
	private String inReplyTo;
	private String replyBy;
	@NotNull
	private SemanticProtocol semanticProtocol;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Participant getSender() {
		return sender;
	}

	public void setSender(Participant sender) {
		this.sender = sender;
	}

	public Participant getReceiver() {
		return receiver;
	}

	public void setReceiver(Participant receiver) {
		this.receiver = receiver;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public Object getMessageId() {
		return messageId;
	}

	public void setMessageId(Object messageId) {
		this.messageId = messageId;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public String getReplyBy() {
		return replyBy;
	}

	public void setReplyBy(String replyBy) {
		this.replyBy = replyBy;
	}

	public SemanticProtocol getSemanticProtocol() {
		return semanticProtocol;
	}

	public void setSemanticProtocol(SemanticProtocol semanticProtocol) {
		this.semanticProtocol = semanticProtocol;
	}

}
