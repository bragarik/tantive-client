package com.challenge.services;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.LoggerFactory;

import com.challenge.entitys.MessageEntity;
import com.challenge.entitys.ProtocolEntity;
import com.challenge.enums.Frame;
import com.challenge.util.CRC8;

/**
 * Business rules class
 * 
 * @author Ricardo Braga
 *
 */
public class AppService extends IoHandlerAdapter {

	private static String HOSTNAME = "127.0.0.1";
	private static final int PORT = 5151;
	final static Scanner scanner = new Scanner(System.in);

	private NioSocketConnector connector;

	private final int port;
	private final String hostname;

	/**
	 * port default <code>5151<br>
	 * hostname default <code>127.0.0.1
	 * 
	 * @param connector
	 */
	public AppService(final NioSocketConnector connector) {
		this.connector = connector;
		hostname = HOSTNAME;
		port = PORT;
	}

	/**
	 * hostname default <code>127.0.0.1
	 * 
	 * @param connector
	 */
	public AppService(final NioSocketConnector connector, final int port) {
		this.connector = connector;
		this.hostname = HOSTNAME;
		this.port = port;
	}

	/**
	 * port default <code>5151
	 * 
	 * @param connector
	 */
	public AppService(final NioSocketConnector connector, final String hostname) {
		this.connector = connector;
		this.hostname = HOSTNAME;
		this.port = PORT;
	}

	/**
	 * 
	 * @param connector
	 * @param hostname
	 * @param port
	 */
	public AppService(final NioSocketConnector connector, final String hostname, final int port) {
		this.connector = connector;
		this.hostname = hostname;
		this.port = port;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		MessageEntity entity = processMessage((ProtocolEntity) message);
		// if there is a response
		if (entity != null) {
			session.write(entity);
		}
		session.closeOnFlush();
	}

	public boolean waitCommands() {
		String commandText = "";
		int command = 0;
		do {
			System.out.println("Escolha a opção de envio, digite:\n");
			System.out.println("1. Mensagem de texto.");
			System.out.println("2. Informações de um usuário.");
			System.out.println("3. Solicitar data e hora atual.\n");
			System.out.println("Para sair digite \'sair:\' ");
			try {
				commandText = scanner.nextLine();
				if (commandText.equalsIgnoreCase("quit") || commandText.equalsIgnoreCase("sair")
						|| commandText.equalsIgnoreCase("exit")) {
					scanner.close();
					return false;
				}

				command = Integer.parseInt(commandText);
			} catch (NumberFormatException e) {
				LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
				System.out.println("!Opção Inválida!\n");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// ignore.
				}
				continue;
			}

			if (command < 0 || command > 3) {
				System.out.println("!Opção Inválida!\n");
				continue;
			}

			try {
				switch (command) {
				case 1:
					System.out.println("Digite a mensagem.");

					commandText = scanner.nextLine();

					sendMessage(commandText);
					break;
				case 2:
					sendInfoUser();
					break;
				case 3:
					getDateTimeServer();
					break;
				}
			} catch (RuntimeIoException e) {
				System.out.println(e.getMessage());
			}

		} while (true);

	}

	private void sendMessage(String commandText) throws RuntimeIoException {
		IoSession session = connect(connector);

		MessageEntity entity = new MessageEntity();

		entity.setFrame(Frame.TEXT_MESSAGE.getValue());
		entity.setData(commandText.getBytes(StandardCharsets.US_ASCII));
		entity.setCrc(CRC8.getValue(entity.getCrcData()));

		session.write(entity);

	}

	private void sendInfoUser() {
		IoSession session = connect(connector);

		MessageEntity entity = new MessageEntity();

		entity.setFrame(Frame.USER_INFO.getValue());
		entity.setData(new byte[] { 0x20, 0x7A, (byte) 0xC3, 0x0C, 0x4D, 0x69, 0x63, 0x68, 0x65, 0x6C, 0x20, 0x52, 0x65,
				0x69, 0x70, 0x73 }); // TODO: add UserInfoEntity.java
		entity.setCrc(CRC8.getValue(entity.getCrcData()));

		session.write(entity);
	}

	private void getDateTimeServer() {
		IoSession session = connect(connector);

		MessageEntity entity = new MessageEntity();

		entity.setFrame(Frame.DATE_TIME.getValue());
		entity.setData(new byte[] { 0x41, 0x6D, 0x65, 0x72, 0x69, 0x63, 0x61, 0x2F, 0x53, 0x61, 0x6F, 0x5F, 0x50, 0x61,
				0x75, 0x6C, 0x6F }); // TODO: add UserInfoEntity.java
		entity.setCrc(CRC8.getValue(entity.getCrcData()));

		session.write(entity);
	}

	private IoSession connect(final NioSocketConnector connector) {
		IoSession session = null;
		try {
			ConnectFuture future = connector.connect(new InetSocketAddress(hostname, port));
			future.awaitUninterruptibly();
			session = future.getSession();
		} catch (RuntimeIoException e) {
			LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
			throw e;
		}
		return session;
	}

	public MessageEntity processMessage(ProtocolEntity protocol) {

		MessageEntity messageEntity = protocol.getMessageEntity();
		if (!checkCRC8(messageEntity)) {
			return null;
		}

		// TODO: debug - tratar respostas
		System.out.println(messageEntity);

		return null;
	}

//TODO: ack pronto mas não utilizado
//	private MessageEntity ackReponse() {
//		MessageEntity write = new MessageEntity();
//
//		write.setBytes((byte) 0x05);
//		write.setFrame(Frame.ACK.getValue());
//		write.setData(new byte[0]);
//		write.setCrc(CRC8.getValue(write.getCrcData()));
//		return write;
//	}

	/**
	 * Check CRC8
	 * 
	 * @param entity
	 * @return CRC8 OK or NOK
	 */
	private boolean checkCRC8(MessageEntity entity) {
		return entity.getCrc() == CRC8.getValue(entity.getCrcData());
	}

}