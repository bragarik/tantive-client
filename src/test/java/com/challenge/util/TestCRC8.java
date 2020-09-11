package com.challenge.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestCRC8 {

	@Test
	public void testUpdateByteArray() {

		// 09 01 31 32 33 34 RR 0D
		// https://crccalc.com/?crc=090131323334&method=crc8&datatype=hex&outtype=hex
		List<Byte> list1 = Arrays.asList((byte) 0x09, (byte) 0x01, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34);
		assertEquals((byte) 0xc6, CRC8.getValue(list1));

		// https://crccalc.com/?crc=185a2f564&method=crc8&datatype=hex&outtype=hex
		// //random number
		List<Byte> list2 = Arrays.asList((byte) 0x18, (byte) 0x5a, (byte) 0x2f, (byte) 0x56, (byte) 0x04);
		assertEquals((byte) 0x21, CRC8.getValue(list2));

	}
}
