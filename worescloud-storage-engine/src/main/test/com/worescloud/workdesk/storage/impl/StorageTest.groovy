package com.worescloud.workdesk.storage.impl

import com.worescloud.workdesk.storage.exception.StorageException
import com.worescloud.workdesk.storage.impl.mock.Address
import com.worescloud.workdesk.storage.impl.mock.Attachment
import com.worescloud.workdesk.storage.impl.mock.Message
import org.joda.time.DateTime
import org.testng.annotations.Test

import java.util.function.Predicate

import static com.worescloud.workdesk.storage.StorageQueryBuilder.StorageQuery
import static com.worescloud.workdesk.storage.impl.dsl.StorageTestImpl.type
import static com.worescloud.workdesk.storage.util.Filters.eq
import static com.worescloud.workdesk.storage.util.Filters.ne

public class StorageTest {

	public static final String MESSAGE_ID = "message-id-002"

	@Test
	public void shouldSave() throws StorageException {
		type(Message)
				.save(defaultMessage(), {MESSAGE_ID})
				.success()
	}

	@Test
	public void shouldSaveOrUpdateWhenSave() throws StorageException {
		type(Message)
				.saveOrUpdate(defaultMessage(), {MESSAGE_ID})
				.find(MESSAGE_ID)
				.match({
					Optional<Message> it ->
						assert it.isPresent()
						return true
				} as Predicate)
	}

	@Test
	public void shouldSaveOrUpdateWhenUpdate() throws StorageException {
		def message = defaultMessage()
		def newMessage = new Message(messageId: MESSAGE_ID, subject: "this is a new subject")

		type(Message)
				.save(message, {MESSAGE_ID})
				.saveOrUpdate(newMessage, {MESSAGE_ID})
				.find(MESSAGE_ID)
				.match({
					Optional<Message> it ->
						assert it.isPresent()
						assert it.get().subject == newMessage.subject
						assert it.get().content == null
						return true
				} as Predicate)
	}

	@Test
	public void shouldDelete() throws StorageException {
		type(Message)
				.save(defaultMessage(), {MESSAGE_ID})
				.delete(MESSAGE_ID)
				.success()
	}

	@Test
	public void shouldUpdate() throws StorageException {
		def message = new Message(
				messageId: MESSAGE_ID,
				subject: "new subject",
				address: new Address(
						personal: "personal",
						address: "a@b.c"
				)
		)

		type(Message)
				.save(defaultMessage(), {MESSAGE_ID})
				.update(message, ['subject', 'address'] as Set)
				.find(MESSAGE_ID)
				.match({
					Optional<Message> it ->
						assert it.isPresent()
						assert it.get().subject == message.subject
						assert it.get().address.personal == message.address.personal
						assert it.get().address.address == message.address.address
						return true
				} as Predicate)
	}

	@Test
	public void shouldNotUpdateTransientField() throws StorageException {
		type(Message)
				.save(defaultMessage(), {MESSAGE_ID})
				.update(new Message(
					attachments: [new Attachment(
						name: "attachment 2",
						data: "attachment content other".getBytes("UTF-8")
					)]
				), ['attachment'] as Set)
				.find(MESSAGE_ID)
				.match({
					Optional<Message> it ->
						assert it.isPresent()
						assert it.get().attachments == null
						return true
				} as Predicate)
	}

	@Test
	public void shouldQueryWithEqFilter() throws StorageException {
		type(Message)
				.save(defaultMessage(), {MESSAGE_ID})
				.query({
					StorageQuery<Message> query ->
					query.filter(eq("subject", "this is a subject"))
							.collect()
				 })
				.match({
					List<Message> messages ->
						assert messages.size() == 1
						assert messages.get(0).messageId == MESSAGE_ID
						return true
				} as Predicate)
	}

	@Test
	public void shouldQueryWithNeFilter() throws StorageException {

		def secondMessage = new Message(
				messageId: "message-id-003",
				subject: "this is a subject 2",
				content: "message content body 2"
		)

		type(Message)
				.save(defaultMessage(), {MESSAGE_ID})
				.save(secondMessage, {secondMessage.messageId})
				.query({
					StorageQuery<Message> query ->
						query.full()
								.filter(ne("messageId", MESSAGE_ID))
								.collect()
				})
				.match({
					List<Message> messages ->
						assert messages.size() == 1
						assert messages.get(0).messageId == secondMessage.messageId
						assert messages.get(0).subject == secondMessage.subject
						return true
				} as Predicate)
	}

	private static defaultMessage = { ->
		new Message(
				messageId: MESSAGE_ID,
				subject: "this is a subject",
				content: "message content body",
				sendTime: new DateTime(),
				attachments: [
						new Attachment(
								name: "attachment 1",
								data: "attachment content".getBytes("UTF-8")
						)
				]
		)
	}

}
