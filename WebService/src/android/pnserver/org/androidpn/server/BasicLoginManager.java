package org.androidpn.server;

import org.androidpn.server.util.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
/**
 * BASIC 认证的JAVA类
 */
public class BasicLoginManager {

	private final static BASE64Decoder DECODER = new BASE64Decoder();
	private final static Map<String, String> USERS = new Hashtable<String, String>();
    private static final Log log = LogFactory.getLog(BasicLoginManager.class);
	
	public static Map<String, String> getUserMap() {
		if (USERS.isEmpty()) {
			USERS.put("username", Config.getString("username"));
			USERS.put("password", Config.getString("password"));
		}
		return USERS;
	}

	public static String decodeBase64(String base64Code) {

		String decode = null;
		if (base64Code != null) {

			try {
				byte[] decodeByte = DECODER.decodeBuffer(base64Code);
				decode = new String(decodeByte);
			} catch (IOException e) {
				decode = null;
				log.error(e);
			}

		}
		return decode;
	}

	public static String validate(String authorization) {

		log.debug("authorization=" + authorization);

		if (authorization == null || authorization.indexOf("Basic") == -1) {
			return null;
		}

		String base64Code = authorization.substring(authorization
				.indexOf("Basic") + 6);

		log.debug("authorization base64Code=" + base64Code);
		String decode = decodeBase64(base64Code);
		log.debug("authorization decode=" + decode);
		String[] arr = decode.split(":");
		if (decode == null || arr.length < 2) {
			return null;
		}
		String username = arr[0];
		String password = arr[1];

		if (username == null || password == null) {
			return null;
		}

		if (username.equals(getUserMap().get("username"))
				&& password.equals(getUserMap().get("password"))) {
			return decode;
		}
		return null;
	}

}
