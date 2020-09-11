package com.challenge.entitys;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.challenge.enums.Frame;

/**
 * Model transitório entre as camadas<br>
 * Porém com algumas particularidades<br>
 * 
 * <table>
 * <thead>
 * <tr>
 * <th>INIT</th>
 * <th>BYTES</th>
 * <th>FRAME</th>
 * <th>DATA</th>
 * <th>CRC</th>
 * <th>END</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>1 byte</td>
 * <td>1 byte</td>
 * <td>1 byte</td>
 * <td>n byte</td>
 * <td>1 byte</td>
 * <td>1 byte</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @author Ricardo Braga
 *
 */
@Entity
@Table()
public class MessageEntity {

	public static final char REQUEST_END = '\r';
	public static final char REQUEST_INT = '\n';

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	private byte bytes;

	private Frame frame;

	private byte[] data;

	private byte crc;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Caso o <code>bytes</code> seja atribuido uma vez, ele nunca será gerado<br>
	 * 
	 * para gerar utilize <code>MessageEntity.getSizeMessage()</code>
	 * 
	 * @return the bytes
	 */
	public byte getBytes() {
		if (bytes == 0) {
			bytes = MessageEntity.getSizeMessage(this);
		}
		return bytes;
	}

	@Column(name = "bytes")
	public int getBytesInt() {
		return (int) getBytes();
	}

	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(byte bytes) {
		this.bytes = bytes;
	}

	/**
	 * @return the frame
	 */
	@Column(name = "frame")
	public byte getFrame() {
		return frame.getValue();
	}

	public Frame getFrameEnum() {
		return frame;
	}

	/**
	 * @param frame the frame to set
	 */
	public void setFrame(byte frame) {
		this.frame = Frame.valueOf(frame);
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	@Column(name = "data")
	public String getDataString() {
		return new String(data, StandardCharsets.US_ASCII);
	}

	/**
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * @return the crc
	 */
	public byte getCrc() {
		return crc;
	}

	/**
	 * @param crc the crc to set
	 */
	public void setCrc(byte crc) {
		this.crc = crc;
	}

	/**
	 * Os campos INIT, CRC e END não devem ser considerados para o cálculo do CRC,
	 * ficando somente os campos BYTES, FRAME e DATA incluídos no cálculo.
	 * 
	 * @return byte[]
	 */
	public List<Byte> getCrcData() {
		List<Byte> bytes = new ArrayList<>();
		bytes.add(getBytes());
		bytes.add(getFrame());
		for (byte b : getData()) {
			bytes.add(b);
		}

		return bytes;
	}

	/**
	 * If data is not null<br>
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static byte getSizeMessage(MessageEntity entity) throws IllegalArgumentException {
		if (entity.getData() != null) {
			// all fields except id field and data array field (2)
			return (byte) (MessageEntity.class.getDeclaredFields().length + (entity.getData().length - 2));
		} else {
			throw new IllegalArgumentException("data cannot be null");
		}
	}

	@Override
	public String toString() {
		return id + ": " + (int) bytes + " - " + String.format("%x", frame.getValue()) + " - " + getDataString() + " - "
				+ String.format("%x", crc);
	}

}
