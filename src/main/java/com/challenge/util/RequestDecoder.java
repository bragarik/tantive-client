package com.challenge.util;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.LoggerFactory;

import com.challenge.entitys.MessageEntity;
import com.challenge.entitys.ProtocolEntity;
import com.challenge.entitys.UserInfoEntity;
import com.challenge.enums.Frame;

/**
 * 
 * @author Ricardo Braga
 *
 */
public class RequestDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		in.setAutoExpand(true);
		in.setAutoShrink(true);
		// if em vez do while no hasRemaining pois não utilizo a fila do IoBuffer
		if (in.hasRemaining()) {
			int i = 0, j = 0, k = 0, l = 0;
			char testIntChar = (char) in.get(i++);//init
			char testEndChar = (char) in.get(in.limit() - 1);
			if (testIntChar != MessageEntity.REQUEST_INT && testEndChar != MessageEntity.REQUEST_END) {
				return false;
			} else {
				ProtocolEntity protocolEntity = new ProtocolEntity();
				MessageEntity messageEntity = new MessageEntity();

				//order to populate
				//init, byte, frame, data, crc and end
				messageEntity.setBytes(in.get(i++));
				messageEntity.setFrame(in.get(i++));

				// - 6 (except init, bytes, frame, crc and end)
				int size = (int) messageEntity.getBytes() - 5;
				byte data[] = new byte[size];
				for (; j < size;) {
					data[j++] = in.get(i++);
				}

				messageEntity.setData(data);

				if(messageEntity.getFrameEnum().equals(Frame.USER_INFO)) {
					UserInfoEntity userInfoEntity = new UserInfoEntity();

					userInfoEntity.setAge(messageEntity.getData()[k++]);
					userInfoEntity.setWeight(messageEntity.getData()[k++]);
					userInfoEntity.setHeight(messageEntity.getData()[k++]);
					userInfoEntity.setNameSize(messageEntity.getData()[k++]);
					userInfoEntity.setName(new byte[(int) userInfoEntity.getNameSize()]);
					try {
						for (; l < (int) userInfoEntity.getNameSize(); l++) {
							userInfoEntity.getName()[l] = messageEntity.getData()[k++];
						}
					} catch (NullPointerException e) {
						LoggerFactory.getLogger(this.getClass())
								.error("The name size does not match: " + e.getMessage(), e);
					}

					protocolEntity.setUserInfoEntity(userInfoEntity);
				}

				messageEntity.setCrc(in.get(i));

				protocolEntity.setMessageEntity(messageEntity);

				in.position(in.limit());
				out.write(protocolEntity);
				return true;

			}
		}
		return false;
	}
}