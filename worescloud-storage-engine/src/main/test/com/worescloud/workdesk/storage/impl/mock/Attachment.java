package com.worescloud.workdesk.storage.impl.mock;

import com.worescloud.workdesk.storage.annotation.Collection;
import com.worescloud.workdesk.storage.annotation.Key;
import org.joda.time.DateTime;

import java.io.Serializable;

@Collection("attachment")
public class Attachment implements Serializable {

	@Key
	private String attachmentId;
	private String messageId;
	private String name;
	private DateTime addTime;
	private byte[] data;

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	public DateTime getAddTime() {
		return addTime;
	}

	public void setAddTime(DateTime addTime) {
		this.addTime = addTime;
	}

	byte[] getData() {
		return data;
	}

	void setData(byte[] data) {
		this.data = data;
	}
}