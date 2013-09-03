package com.rong360.creditassitant.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * utils for handle network operation.
 */
public class NetUtil {
    private static final String TAG = "NetUtil";
    private static final String CONTENT_CHARSET = "UTF-8";

    public static byte[] gzipCompress(byte[] data) throws IOException {
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	GZIPOutputStream gos = null;
	try {
	    gos = new GZIPOutputStream(os);
	    gos.write(data);
	    gos.finish();
	    return os.toByteArray();
	} finally {
	    os.close();
	    if (gos != null)
		gos.close();
	}

    }

    public static byte[] gzipDecompress(byte[] compressed) throws IOException {
	ByteArrayInputStream is = new ByteArrayInputStream(compressed);
	GZIPInputStream gis = null;
	try {
	    gis = new GZIPInputStream(is, 1024);
	    byte[] data = new byte[1024];
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    int len = 0;
	    while ((len = gis.read(data)) != -1) {
		bos.write(data, 0, len);
	    }
	    return bos.toByteArray();
	} finally {
	    is.close();
	    if (gis != null)
		gis.close();
	}
    }
}
