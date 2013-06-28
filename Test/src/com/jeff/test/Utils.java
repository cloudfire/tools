package com.jeff.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class Utils {
	/*private final static ObjectFactory<CRC32> crc32Factory = ObjectFactory.getInstance(CRC32.class);
	public static CRC32 newCRC32() {
		return crc32Factory.object();
	}
	public static void recycleCRC32(CRC32 o) {
		o.reset();
		crc32Factory.recycle(o);
	}
	public static long crc32(byte[] b) {
		CRC32 o = newCRC32();
		o.update(b);
		long r = o.getValue();
		recycleCRC32(o);
		return r;
	}*/
	
	private static final Logger logger = Logger.getLogger(Utils.class);
	private static final Lock md5Lock = new ReentrantLock();
	private static final Lock sha1Lock = new ReentrantLock();
	public static MessageDigest md5;
	public static MessageDigest sha1;
/*	public static final byte[] hexDigits = new byte[] {C0, C1, C2, C3, C4, C5,
		C6, C7, C8, C9, Ca, Cb, Cc, Cd, Ce, Cf};*/
	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
			sha1 = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			;
		}
	}
	
	public static long currentTime() {
		return (long) (System.currentTimeMillis() / 1000);
	}
	
	// must sync when access
	private static int syncNextId = 1;
	public static synchronized int getNextId() {
		return syncNextId++;
	}
	
	public static byte[] md5(byte[] buf) {
		md5Lock.lock();
		try {
			byte[] r = md5.digest(buf);
			md5.reset();
			return r;
		} finally {
			md5Lock.unlock();
		}
	}
	
	public static byte[] sha1(byte[] buf) {
		sha1Lock.lock();
		try {
			byte[] r = sha1.digest(buf);
			sha1.reset();
			return r;
		} finally {
			sha1Lock.unlock();
		}
	}
	
	public static String httpGetContent(String url, int timeout) {
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			// MalformedURLException, IOException
			
				logger.error("error_reading " + url, e);
			
			return null;
		}
	}
	
	private static String fileGetContent(BufferedReader reader) {
		try {
			StringBuilder sb = new StringBuilder();
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			// IOException
			return null;
		}
	}
	
	public static String fileGetContent(InputStream in) {
		String r = null;
		try {
			r = fileGetContent(new BufferedReader(new InputStreamReader(in)));
		} catch (Exception e) {
			r = null;
		}
		// FileNotFoundException
		if (r == null ) {
			logger.error("fileGetContent error_reading " + in.toString());
		}
		return r;
	}
	
	public static String fileGetContent(String path) {
		String r = null;
		try {
			r = fileGetContent(new BufferedReader(new FileReader(path)));
		} catch (Exception e) {
			r = null;
		}
		// FileNotFoundException
		if (r == null) {
			logger.error("fileGetContent error_reading " + path);
		}
		return r;
	}
	
	public static long ip2long(String strIP) {
		try {
			long[] ip = new long[4];
			int position1 = strIP.indexOf(".");
			int position2 = strIP.indexOf(".", position1 + 1);
			int position3 = strIP.indexOf(".", position2 + 1);

			ip[0] = Long.parseLong(strIP.substring(0, position1));
			ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
			ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
			ip[3] = Long.parseLong(strIP.substring(position3 + 1));
			return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
		} catch (Exception e) {
			return 0;
		}
	}

	public static String long2ip(long longIP) {
		return new StringBuffer(String.valueOf(longIP >>> 24)).append(".")
			.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16)).append(".")
			.append(String.valueOf((longIP & 0x0000FFFF) >>> 8)).append(".")
			.append(String.valueOf(longIP & 0x000000FF)).toString();
	}
	
	public static byte[] long2bytes(long v) {
		int l = 1;
		for (long vv = v; vv >= 10; l++) {
			vv /= 10;
		}
		byte[] bs = new byte[l];
		for (int i = 1; i <= l; i++) { // down to up
			bs[l - i] = (byte) (v - v / 10 * 10 + 48);
			v = v / 10;
		}
		return bs;
	}
	
	public static long bytes2long(byte[] bs, int start, int l) {
		long r = 0;
		for (int i = 0; i < l; i++) {
			r += (int) (Math.pow(10, l - i - 1) * (bs[start + i] - 48)); // 48 = 0, 49 = 1
		}
		return r;
	}
	
	public static long bytes2long(byte[] bs) {
		return bytes2long(bs, 0, bs.length);
	}
	
	public static byte[] int2bytes(int v) {
		int l = 1;
		for (int vv = v; vv >= 10; l++) {
			vv /= 10;
		}
		byte[] bs = new byte[l];
		for (int i = 1; i <= l; i++) { // down to up
			bs[l - i] = (byte) (v - v / 10 * 10 + 48);
			v = v / 10;
		}
		return bs;
	}
	
	public static int bytes2int(byte[] bs, int start, int l) {
		int r = 0;
		for (int i = 0; i < l; i++) {
			r += (int) (Math.pow(10, l - i - 1) * (bs[start + i] - 48)); // 48 = 0, 49 = 1
		}
		return r;
	}
	
//	public static int bytes2int(ByteBuffer bs, int start, int l) {
//		int r = 0;
//		for (int i = 0; i < l; i++) {
//			r += (int) (Math.pow(10, l - i - 1) * (bs.get(start + i) - 48)); // 48 = 0, 49 = 1
//		}
//		return r;
//	}
	
	public static int bytes2int(byte[] bs) {
		return bytes2int(bs, 0, bs.length);
	}

	
	/*public static byte[] bin2hex(byte[] buf) {
		byte[] r = new byte[buf.length * 2];
		for (int i = 0; i < buf.length; i++) {
			r[i * 2] = hexDigits[buf[i] >>> 4 & 0xf];
			r[i * 2 + 1] = hexDigits[buf[i] & 0xf];
		}
		return r;
	}*/
	
}
