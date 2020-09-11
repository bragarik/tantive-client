package com.challenge.services;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.LoggerFactory;

import com.challenge.dao.GenericDao;
import com.challenge.entitys.MessageEntity;
import com.challenge.entitys.ProtocolEntity;
import com.challenge.entitys.UserInfoEntity;
import com.challenge.enums.Frame;
import com.challenge.util.CRC8;

/**
 * 
 * @author Ricardo Braga
 *
 */
public class AppService extends IoHandlerAdapter {

	private static String HOSTNAME = "127.0.0.1";
	private static final int PORT = 5151;
	final static Scanner scanner = new Scanner(System.in);

	private NioSocketConnector connector;

	public AppService(final NioSocketConnector connector) {
		this.connector = connector;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		MessageEntity entity = processMessage((ProtocolEntity) message);
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

		} while (true);

	}

	private void sendMessage(String commandText) {
		IoSession session = connect(connector);

		
		MessageEntity entity = new MessageEntity();
		
		entity.setFrame(Frame.TEXT_MESSAGE.getValue());
		entity.setData(commandText.getBytes(StandardCharsets.US_ASCII));
		entity.setBytes(MessageEntity.getSizeMessage(entity));
		entity.setCrc(CRC8.getValue(entity.getCrcData()));


		session.write(entity);
		
	}

	private void sendInfoUser() {
		IoSession session = connect(connector);
		
		MessageEntity entity = new MessageEntity();
		
		entity.setFrame(Frame.USER_INFO.getValue());
		entity.setData(new byte[] {0x20, 0x7A, (byte) 0xC3, 0x0C, 0x4D, 0x69, 0x63, 0x68, 0x65, 0x6C, 0x20, 0x52, 0x65, 0x69, 0x70, 0x73 }); //TODO: add UserInfoEntity.java
		entity.setBytes(MessageEntity.getSizeMessage(entity));
		entity.setCrc(CRC8.getValue(entity.getCrcData()));

		
		session.write(entity);
	}

	private void getDateTimeServer() {
		IoSession session = connect(connector);
		
		MessageEntity entity = new MessageEntity();
		
		entity.setFrame(Frame.DATE_TIME.getValue());
		entity.setData(new byte[] {0x41, 0x6D, 0x65, 0x72, 0x69, 0x63, 0x61, 0x2F, 0x53, 0x61, 0x6F, 0x5F, 0x50, 0x61, 0x75, 0x6C, 0x6F }); //TODO: add UserInfoEntity.java
		entity.setBytes(MessageEntity.getSizeMessage(entity));
		entity.setCrc(CRC8.getValue(entity.getCrcData()));

		
		session.write(entity);
	}

	private static IoSession connect(final NioSocketConnector connector) {
		IoSession session = null;
		try {
			ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
			future.awaitUninterruptibly();
			session = future.getSession();
		} catch (RuntimeIoException e) {
			LoggerFactory.getLogger(AppService.class).error(e.getMessage(), e);
		}
		return session;
	}

	public MessageEntity processMessage(ProtocolEntity protocol) {

		MessageEntity messageEntity = protocol.getMessageEntity();
		if (!checkCRC8(messageEntity)) {
			return null;
		}

		System.out.println(messageEntity);
		
//		switch (Frame.valueOf(messageEntity.getFrame())) {
//		case ACK:
//			return null;
//		case TEXT_MESSAGE:
////			return handleTextMessage(protocol);
//		case USER_INFO:
////			return handleUserInfo(protocol);
//		case DATE_TIME:
////			return handleDateTime(protocol);
//		}

		return null;
	}

	private MessageEntity handleTextMessage(ProtocolEntity protocol) {

		MessageEntity messageEntity = protocol.getMessageEntity();
		if (!checkCRC8(messageEntity)) {
			return null;
		}

		// Persistence
		new Thread(daoSaveRunnable(messageEntity)).start();

		// response
		return ackReponse();
	}

	private MessageEntity handleUserInfo(ProtocolEntity protocol) {

		MessageEntity messageEntity = protocol.getMessageEntity();
		if (!checkCRC8(messageEntity)) {
			return null;
		}

		// Persistence
		new Thread(daoSaveRunnable(messageEntity)).start();
		new Thread(daoSaveRunnable(protocol.getUserInfoEntity())).start();

		// response
		return ackReponse();
	}

	private MessageEntity handleDateTime(ProtocolEntity protocol) {

		MessageEntity messageEntity = protocol.getMessageEntity();

		if (!checkCRC8(messageEntity)) {
			return null;
		}

		// Persistence
		new Thread(daoSaveRunnable(messageEntity)).start();

		MessageEntity response = new MessageEntity();
		response.setFrame(Frame.DATE_TIME.getValue());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd;MM;yy;HH;mm;ss");

		// set to your timezone
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone(messageEntity.getDataString()));

		//
		String[] dataArray = simpleDateFormat.format(calendar.getTime()).split(";");

		// SimpleDateFormat - 2 bytes masks
		byte[] byteArray = new byte[dataArray.length * 2];
		for (int i = 0, j = 0; i < dataArray.length * 2; j++) {
			byteArray[i++] = (byte) dataArray[j].getBytes(StandardCharsets.US_ASCII)[0];
			byteArray[i++] = (byte) dataArray[j].getBytes(StandardCharsets.US_ASCII)[1];
		}

		response.setData(byteArray);

		response.setBytes(MessageEntity.getSizeMessage(response));
		response.setCrc(CRC8.getValue(response.getCrcData()));

		// response
		return response;
	}

	private MessageEntity ackReponse() {
		MessageEntity write = new MessageEntity();

		write.setBytes((byte) 0x05);
		write.setFrame(Frame.ACK.getValue());
		write.setData(new byte[0]);
		write.setCrc(CRC8.getValue(write.getCrcData()));
		return write;
	}

	/**
	 * Check CRC8
	 * 
	 * @param entity
	 * @return CRC8 OK or NOK
	 */
	private boolean checkCRC8(MessageEntity entity) {
		return entity.getCrc() == CRC8.getValue(entity.getCrcData());
	}

	/**
	 * 
	 * @param <E>
	 * @param entity
	 * @return
	 */
	private static <E> Runnable daoSaveRunnable(E entity) {
		return new Runnable() {
			public void run() {
				try {
					GenericDao.save(entity);
				} catch (Exception e) {
					LoggerFactory.getLogger(GenericDao.class).error(e.getMessage(), e);
				}
			}
		};
	}

}