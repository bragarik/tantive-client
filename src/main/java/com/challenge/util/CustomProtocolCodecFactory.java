package com.challenge.util;
import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.textline.TextLineEncoder;

public class CustomProtocolCodecFactory extends TextLineEncoder implements ProtocolCodecFactory {
	
	public CustomProtocolCodecFactory(Charset charset) {
		super(charset);
	}
	
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return new RequestEncoder();
    }
    
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return new RequestDecoder();
    }
}