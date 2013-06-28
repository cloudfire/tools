package com.jeff.test;

/**
 * @require Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6
 * @author cgao
 *
 */
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.jeff.test.IdEncrypter;

/**
 * php/java -> 755.887552485/3184.7133757961783/3974.56279809221[useGlobals]/
 * 25575.447570332482[includeLock]
 * 
 * @author cgao fujie(copy from tudou)
 * @TODO i don't know if global.iv etc can thread safe
 */
public class TudouUtils {
	private static final Logger logger = Logger.getLogger(TudouUtils.class);
	private static final Lock encodeLock = new ReentrantLock();
	private static final Lock decodeLock = new ReentrantLock();
	private static final byte[] idCrypterSaltBytes = "H0w_many_r0ad_a_man_must_been_wa1k_d0wn, before_they_ca11_him_a_man."
			.getBytes(Charset.forName("ISO-8859-1"));
	private static Blowfish blowfish;

	static {
		blowfish = new Blowfish(Arrays.copyOf(idCrypterSaltBytes, 32),
				Arrays.copyOfRange(idCrypterSaltBytes, 32, 40));
	}

	/**
	 * @Description：将土豆的数字vid 加密为字符串
	 * @author <a href="mailto:fujie@youku.com">jeff</a>
	 * @param 视频数字id
	 * @return 视频id字符串
	 */
	public static String encodeVid(int id) {
		try {
			byte[] sourceBytes = new byte[8];
			// id 小于1亿
			if (id < 100000000) {
				int2bytes(sourceBytes, id);
			} else {
				sourceBytes[0] = (byte) ((id >> 24) & 0xFF);
				sourceBytes[1] = (byte) ((id >> 16) & 0xFF);
				sourceBytes[2] = (byte) ((id >> 8) & 0xFF);
				sourceBytes[3] = (byte) ((id) & 0xFF);
				sourceBytes[4] = Byte.MAX_VALUE;
				sourceBytes[5] = Byte.MAX_VALUE;
				sourceBytes[6] = Byte.MAX_VALUE;
				sourceBytes[7] = Byte.MAX_VALUE;
			}
			byte[] encryptedBytes;
			encodeLock.lock();
			try {
				encryptedBytes = blowfish.encrypt(sourceBytes);
			} finally
			{
				encodeLock.unlock();
			}
			
			char[] base64Chars = Base64.encode(encryptedBytes);
			int i;
			for (i = 0; i < base64Chars.length; i++) {
				if (base64Chars[i] == '=') {
					break; // = only appear at end, so i is the ending
				} else if (base64Chars[i] == '+') {
					base64Chars[i] = '-';
				} else if (base64Chars[i] == '/') {
					base64Chars[i] = '_';
				}
			}
			return new String(base64Chars, 0, i);
		} catch (Exception e) {

			logger.info("error in IdEncrypter", e);

			return null;
		}
	}

	/**
	 * @Description：将土豆的视频id串解码为数字id
	 * @author <a href="mailto:fujie@youku.com">jeff</a>
	 * @param 视频id串
	 * @return 数字id
	 */
	public static int decodeVid(String code) {
		try {
			StringBuilder sb = new StringBuilder(code);
			int i = code.length();
			while (i % 4 != 0) {
				sb.append('=');
				i++;
			}
			for (i = 0; i < sb.length(); i++) {
				if (sb.charAt(i) == '-') {
					sb.setCharAt(i, '+');
				} else if (sb.charAt(i) == '_') {
					sb.setCharAt(i, '/');
				}
			}
			byte[] sourceBytes = Base64.decode(sb.toString());
			byte[] decryptedBytes;
			decodeLock.lock();
			try {
				decryptedBytes = blowfish.decrypt(sourceBytes);
			}finally{
				decodeLock.unlock();
			}

			if (decryptedBytes.length == 8
					&& decryptedBytes[7] == Byte.MAX_VALUE) {
				ByteBuffer bf = ByteBuffer.wrap(decryptedBytes);
				return bf.getInt();
			} else {
				for (i = 0; i < decryptedBytes.length; i++) {
					if (decryptedBytes[i] == 0) {
						break;
					}
				}
				return bytes2int(decryptedBytes, 0, i);
			}
		} catch (Exception e) {

			logger.error("error in IdEncrypter", e);

			return 0;
		}
	}

	private static void int2bytes(byte[] bs, int v) {
		int l = 1;
		for (int vv = v; vv >= 10; l++) {
			vv /= 10;
		}
		for (int i = 1; i <= l; i++) { // down to up
			bs[l - i] = (byte) (v - v / 10 * 10 + 48);
			v = v / 10;
		}
	}

	private static int bytes2int(byte[] bs, int start, int l) {
		int r = 0;
		for (int i = 0; i < l; i++) {
			r += (int) (Math.pow(10, l - i - 1) * (bs[start + i] - 48)); // 48 =
																			// 0,
																			// 49
																			// =
																			// 1
		}
		return r;
	}

	public static void main(String[] args) {

		StopWatch st = new StopWatch();
		st.start();
		
		   
 
		for (int i = 0; i < 1000000000; i++) {
			
			if(i%1000000==0){
				System.out.println("count:"+i);
			}
			
			if(!TudouUtils.encodeVid(i).equals(IdEncrypter.encrypt(i))){
				System.out.println("error:"+i);
			}
		}
		
		
//		System.out.println(st.getTime());

	}
	
	

}
