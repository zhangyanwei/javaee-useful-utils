package com.worescloud.workdesk.storage.impl.mock;

import com.worescloud.workdesk.storage.annotation.Collection;
import com.worescloud.workdesk.storage.annotation.Key;
import com.worescloud.workdesk.storage.annotation.Transient;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

@Collection("message")
public class Message implements Serializable {

	@Key
	private String messageId;
	private String subject;
	private String content;
	private Address address;
	private DateTime sendTime;

	private transient String dynamicId;

	@Transient
	private List<Attachment> attachments;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public DateTime getSendTime() {
		return sendTime;
	}

	public void setSendTime(DateTime sendTime) {
		this.sendTime = sendTime;
	}

	public String getDynamicId() {
		return dynamicId;
	}

	public void setDynamicId(String dynamicId) {
		this.dynamicId = dynamicId;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
}