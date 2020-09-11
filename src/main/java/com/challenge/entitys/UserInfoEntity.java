package com.challenge.entitys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.LoggerFactory;

import com.challenge.TCPClient;

/**
 * 
 * @author Ricardo Braga
 *
 */
@Entity
@Table()
public class UserInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "age")
	private byte age;
	
	@Column(name = "weight")
	private byte weight;

	@Column(name = "height")
	private byte height;

	@Column(name = "nameSize")
	private byte nameSize;

	private byte[] name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte getAge() {
		return age;
	}

	public void setAge(byte age) {
		this.age = age;
	}

	public byte getWeight() {
		return weight;
	}

	public void setWeight(byte weight) {
		this.weight = weight;
	}

	public byte getHeight() {
		return height;
	}

	public void setHeight(byte height) {
		this.height = height;
	}

	public byte getNameSize() {
		return nameSize;
	}

	public void setNameSize(byte nameSize) {
		this.nameSize = nameSize;
	}

	public byte[] getName() {
		return name;
	}

	@Column(name = "name")
	public String getNameString() {
		try {
			return new String(name, "ASCII");
		} catch (Exception e) {
			LoggerFactory.getLogger(TCPClient.class).error(e.getMessage(), e);
			return "";
		}
	}

	public void setName(byte[] name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return id + ": " + getNameString() + (int) age;
	}

}
