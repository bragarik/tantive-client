package com.challenge.entitys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class TestMessageEntity {

	@Test
	public void testGetSizeMessage() {

		MessageEntity entity = new MessageEntity();

		// data null (not defined)
		assertThrows(IllegalArgumentException.class, () -> entity.getBytes());

		// data empty = 5 fields
		// int, bytes, frame, crc and end
		byte[] b1 = new byte[0];
		entity.setData(b1);
		assertEquals((byte) 5, entity.getBytes());
		
		
		// data size = 6, 5 fields + 6 data = 11
		// int, bytes, frame, data[6], crc and end
		byte[] b2 = new byte[] {0, 0, 0, 0, 0, 0};
		entity.setData(b2);
		entity.setBytes(MessageEntity.getSizeMessage(entity));
		assertEquals((byte) 11, entity.getBytes());
		
		
		// data size = 1, 5 fields + 1 data = 6
		// int, bytes, frame, data[1], crc and end
		byte[] b3 = new byte[] {0};
		entity.setData(b3);
		entity.setBytes(MessageEntity.getSizeMessage(entity));
		assertNotEquals((byte) 11, entity.getBytes());
	}

}
