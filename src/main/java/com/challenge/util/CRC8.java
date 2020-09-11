package com.challenge.util;

import java.util.List;

/**
 * 
 * @author Ricardo Braga
 *
 */
public class CRC8 {

	public static final byte CRC_POLYNOM = 0x07;

	public static final byte CRC_INITIAL = 0x00;

	/**
	 * static class
	 */
	private CRC8() {
	}

	private static byte update(List<Byte> buffer, int offset, int len) {
		byte crc = CRC_INITIAL;
		int i, j;
		for (i = 0; i < len; i++) {
			crc ^= buffer.get(i);
			for (j = 0; j < 8; j++) {
				if ((crc & 0x80) != 0)
					crc = (byte) ((crc << 1) ^ CRC_POLYNOM);
				else
					crc <<= 1;
			}
		}
		return crc;
	}

	/**
	 * 
	 * @param buffer
	 * @return crc
	 */
	public static byte getValue(List<Byte> buffer) {
		return (byte) (update(buffer, 0, buffer.size()) & 0xff);
	}

}