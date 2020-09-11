
package com.challenge;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.LoggerFactory;

import com.challenge.services.AppService;
import com.challenge.util.CustomProtocolCodecFactory;

public class TCPClient {

	private static int TIMEOUT = 1000;

	/**
	 * Usage args: <code>port</code> or <code>hostname</code> or
	 * <code>hostname port</code>
	 * 
	 * @param port hostname
	 */
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

		AppService service;

		// Usage args: <code>port</code> or <code>hostname</code> or <code>hostname
		// port</code>
		switch (args.length) {
		case 1:
			if (isValidIPV4(args[0])) {
				service = new AppService(connector, args[0]);
			} else {
				int port;
				try {
					port = Integer.parseInt(args[0]);
					service = new AppService(connector, port);
				} catch (NumberFormatException e) {
					LoggerFactory.getLogger(TCPClient.class).warn("Ignored args: " + e.getMessage(), e);
					service = new AppService(connector);
				}
			}
			break;
		case 2:
			if (isValidIPV4(args[0])) {
				try {
					service = new AppService(connector, args[0], Integer.parseInt(args[1]));
				} catch (NumberFormatException e) {
					LoggerFactory.getLogger(TCPClient.class).warn("Ignored args: " + e.getMessage(), e);
					service = new AppService(connector);
				}
			} else {
				LoggerFactory.getLogger(TCPClient.class).warn("Ignored args.");
				service = new AppService(connector);
			}
			break;
		default:
			LoggerFactory.getLogger(TCPClient.class).warn("Ignored args.");
			service = new AppService(connector);
			break;

		}

		connector.setHandler(service);

		if (!service.waitCommands()) {
			System.exit(0);
		}

	}

	private static boolean isValidIPV4(final String s) {

		final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
		Pattern pattern = Pattern.compile(IPV4_REGEX);

		return pattern.matcher(s).matches();
	}
}
