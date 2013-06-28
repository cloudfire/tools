package com.jeff.test;

/**
 * @require Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6
 * @author cgao
 *
 */
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.tudou.util.encry.Base64Coder;

/**
 * php/java ->
 * 755.887552485/3184.7133757961783/3974.56279809221[useGlobals]/25575.447570332482[includeLock]
 * 
 * @author cgao
 * @TODO i don't know if global.iv etc can thread safe
 */
public class IdEncrypter
{
	private static final Logger logger = Logger.getLogger(IdEncrypter.class);
	public static final Charset latin1Charset = Charset.forName("ISO-8859-1");
	private static final Lock encodeLock = new ReentrantLock();
	private static final Lock decodeLock = new ReentrantLock();
	public static final byte[] idCrypterSaltBytes = "H0w_many_r0ad_a_man_must_been_wa1k_d0wn, before_they_ca11_him_a_man."
			.getBytes(latin1Charset);
	public static final String idCrypterAlgorithm = "Blowfish";
	public static final String idCrypterFullAlgorithm = "Blowfish/CBC/NOPADDING";
	public static final Key idCrypterKey = new SecretKeySpec(idCrypterSaltBytes, 0, 32, idCrypterAlgorithm);
	public static Cipher idCrypterEncodeCipher;
	public static Cipher idCrypterDecodeCipher;
	public static final AlgorithmParameterSpec idCrypterIv = new IvParameterSpec(idCrypterSaltBytes, 32, 8);
	
	static
	{
		try
		{
			idCrypterEncodeCipher = Cipher.getInstance(idCrypterFullAlgorithm);
			idCrypterEncodeCipher.init(Cipher.ENCRYPT_MODE, idCrypterKey, idCrypterIv);
			idCrypterDecodeCipher = Cipher.getInstance(idCrypterFullAlgorithm);
			idCrypterDecodeCipher.init(Cipher.DECRYPT_MODE, idCrypterKey, idCrypterIv);
			
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace();
		}
		catch (InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
		}
	}

	public static String encrypt(int id)
	{
		try
		{
			Cipher cipher = idCrypterEncodeCipher;
			byte[] sourceBytes = null;
			if (id < 100000000)
			{ // 小于1亿的时候采用旧的算法
				sourceBytes = Utils.int2bytes(id);
				if (sourceBytes.length % 8 != 0)
				{
					sourceBytes = Arrays.copyOf(sourceBytes, (sourceBytes.length / 8) * 8 + 8);
				}
			}
			else
			{
				// 大于1亿的时候，采用正常的Integer to byte数组的算法
				// 并将数组下标为4-7的元素设置为Byte的最大值(127)
				ByteBuffer bf = ByteBuffer.allocate(4);
				bf.putInt(id);
				sourceBytes = bf.array();
				byte[] bb = new byte[8];
				for (int i = 0; i < 8; i++)
				{
					if (i >= 4)
					{
						bb[i] = Byte.MAX_VALUE;
					}
					else
					{
						bb[i] = sourceBytes[i];
					}
				}
				sourceBytes = bb;
			}
			byte[] encryptedBytes;
			encodeLock.lock();
			try
			{
				encryptedBytes = cipher.doFinal(sourceBytes);
			}
			finally
			{
				encodeLock.unlock();
			}
			char[] base64Chars = Base64Coder.encode(encryptedBytes);
			int i;
			for (i = 0; i < base64Chars.length; i++)
			{
				if (base64Chars[i] == '=')
				{
					break; // = only appear at end, so i is the ending
				}
				else if (base64Chars[i] == '+')
				{
					base64Chars[i] = '-';
				}
				else if (base64Chars[i] == '/')
				{
					base64Chars[i] = '_';
				}
			}
			return new String(base64Chars, 0, i);
		}
		catch (Exception e)
		{

			logger.info("error in IdEncrypter", e);

			return null;
		}
	}

	public static int decrypt(String code)
	{
		try
		{
			Cipher cipher = idCrypterDecodeCipher;
			StringBuilder sb = new StringBuilder(code);
			int i = code.length();
			while (i % 4 != 0)
			{
				sb.append('=');
				i++;
			}
			for (i = 0; i < sb.length(); i++)
			{
				if (sb.charAt(i) == '-')
				{
					sb.setCharAt(i, '+');
				}
				else if (sb.charAt(i) == '_')
				{
					sb.setCharAt(i, '/');
				}
			}
			byte[] sourceBytes = Base64Coder.decode(sb.toString());
			byte[] decryptedBytes;
			decodeLock.lock();
			try
			{
				decryptedBytes = cipher.doFinal(sourceBytes);
			}
			finally
			{
				decodeLock.unlock();
			}

			if (decryptedBytes.length == 8 && decryptedBytes[7] == Byte.MAX_VALUE)
			{
				ByteBuffer bf = ByteBuffer.wrap(decryptedBytes);
				return bf.getInt();
			}
			else
			{
				for (i = 0; i < decryptedBytes.length; i++)
				{
					if (decryptedBytes[i] == 0)
					{
						break;
					}
				}
				return Utils.bytes2int(decryptedBytes, 0, i);
			}
		}
		catch (Exception e)
		{

			logger.error("error in IdEncrypter", e);

			return 0;
		}
	}
	
	public static void main(String[] args) {
		
		StopWatch st =new StopWatch();
		st.start();
		for (int i = 0; i <1000000; i++) {
			IdEncrypter.encrypt(i);
		}
	    System.out.println(st.getTime());
		
	}
}
