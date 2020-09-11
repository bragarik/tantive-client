
package com.challenge;

import java.nio.charset.StandardCharsets;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.challenge.services.AppService;
import com.challenge.util.CustomProtocolCodecFactory;

public class TCPClient {

	private static int TIMEOUT = 1000;

	public static void main(String[] args) {

		System.out.printf(
"		          ___/O   O\\____ \n" +
"		         / O        O   \\ \n" +
" 		         \\______________/ \n" +
"		    -===|____\\///\\\\\\/_____ \n" +
"		        \\----------------/ \n" +
"		         \\______________/  \\/ \n" +
"		          /\\__________/    // \n" +
"		   >=o\\  // //\\\\   || \\\\  // \n" +
"		      \\\\o/ //  \\o  ||  \\o// \n" +
"		          //    || || \n" +
"		      /o==o     |o \\o==o    \n" +
"		     //         //     \\\\ \n" +
"		     /\\        //       /\\   Tantive (TCP Client) \n" +
" 		              /\\              \u001B[30m\u001B[47m- Ricardo Braga - \u001B[0m\n\n\n" 
				);
		

		NioSocketConnector connector = new NioSocketConnector();

		connector.setConnectTimeoutMillis(TIMEOUT);

		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new CustomProtocolCodecFactory(StandardCharsets.US_ASCII)));
		connector.getFilterChain().addLast("logger", new LoggingFilter());

		AppService service = new AppService(connector);
		connector.setHandler(service);

		if(!service.waitCommands()) {
			System.exit(0);
		};

	}
}
