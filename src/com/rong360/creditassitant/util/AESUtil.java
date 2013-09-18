package com.rong360.creditassitant.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESUtil {

    private static final String UTF_8 = "UTF-8";

    private static final char[] HEXCHARS = "0123456789abcdef".toCharArray();

    private static final byte[] defaultIV = { 127, 24, 123, 23, 93, 7, 15, 0,
	    9, 4, 8, 15, 16, 23, 42, 1 };

    private static int valueOf(char c) {
	c = Character.toLowerCase(c);
	if (c >= '0' && c <= '9') {
	    return c - '0';
	} else if (c >= 'a' && c <= 'f') {
	    return c - 'a' + 10;
	}
	return -1;
    }

    public static byte[] hexDecode(String data) {
	byte[] r = new byte[data.length() / 2];

	char[] a = data.toCharArray();

	for (int i = 0; i < a.length; i += 2) {
	    char c1 = a[i], c2 = a[i + 1];
	    int v1 = valueOf(c1);
	    int v2 = valueOf(c2);
	    r[i >> 1] = (byte) ((((v1 & 0xf) << 4) | (v2 & 0xf)) & 0xff);
	}

	return r;
    }

    public static String hexEncode(byte[] data) {
	StringBuffer r = new StringBuffer();
	for (int i = 0; i < data.length; i++) {
	    byte v = data[i];
	    r.append(HEXCHARS[(v >> 4) & 0xf]).append(HEXCHARS[v & 0xf]);
	}
	return r.toString();
    }

    private static SecretKeySpec getSecretKey(String key) {
	MessageDigest digest;
	try {
	    digest = MessageDigest.getInstance("md5");
	    SecretKeySpec keySpec =
		    new SecretKeySpec(digest.digest(key.getBytes(UTF_8)), "AES");
	    return keySpec;
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static byte[] encrypt(byte[] content, String key) {
	try {
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    IvParameterSpec ivs = new IvParameterSpec(defaultIV);
	    cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key), ivs);

	    return cipher.doFinal(content);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static byte[] decrypt(byte[] content, String key) {
	try {
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    IvParameterSpec ivs = new IvParameterSpec(defaultIV);
	    cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key), ivs);

	    return cipher.doFinal(content);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static String encryptHex(String content, String key) {
	try {
	    byte[] res = encrypt(content.getBytes(UTF_8), key);
	    return hexEncode(res);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static String decryptHex(String content, String key) {
	try {
	    byte[] contentBytes = hexDecode(content);
	    byte[] result = decrypt(contentBytes, key);
	    return new String(result, UTF_8);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static String encryptBase64(String content, String key) {
	try {
	    byte[] res = encrypt(content.getBytes(UTF_8), key);
	    return new String(Base64.encodeBase64(res), UTF_8);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static String decryptBase64(String content, String key) {
	try {
	    byte[] contentBytes = Base64.decodeBase64(content.getBytes(UTF_8));
	    byte[] result = decrypt(contentBytes, key);
	    return new String(result, UTF_8);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static String encryptHex(String content, String username, String password) {
	return encryptHex(content, password + username);
    }

    public static String decryptHex(String content, String username, String password) {
	return decryptHex(content, password + username);
    }
}
