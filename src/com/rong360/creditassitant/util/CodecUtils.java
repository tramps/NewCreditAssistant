package com.rong360.creditassitant.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class CodecUtils {
    public static String md5Hex(String input) {
	return new String(Hex.encodeHex(DigestUtils.md5(input)));
    }
}
