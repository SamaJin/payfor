package com.helphand.ccdms.commons;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.LobHelper;

/**
 * 工具类
 */
public class common_util {
	// public LobHelper lobHelper = null;

	public common_util() {
		/*
		 * Configuration cfg = new Configuration().configure(); SessionFactory
		 * sf = cfg.buildSessionFactory(); Session session = sf.openSession();
		 * 
		 * lobHelper = session.getLobHelper();
		 */
	}

	/**
	 * 判断是否为空字符串 或者NULL
	 * 
	 * @param str
	 * @return 为空字符串 或NULL 返回true
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim())
				|| "undefined".equals(str.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 日期转换 String to Date[yyyy-MM-dd HH:mm:ss]
	 * 
	 * @param date
	 * @return
	 */
	public static Date dateFormatSTD(String date) {
		if (common_util.isEmpty(date)) {
			return null;
		}

		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date resultDate = df.parse(date);
			return resultDate;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 日期转换 Date to String[yyyy-MM-dd HH:mm:ss]
	 * 
	 * @param date
	 * @return
	 */
	public static String dateFormatDTS(Date date) {
		if (date == null) {
			return null;
		}

		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String resultDate = df.format(date);
			return resultDate;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            转换规则
	 * @return
	 */
	public static String dateFormatDTS(Date date, String pattern) {
		if (date == null || common_util.isEmpty(pattern)) {
			return null;
		}

		try {
			DateFormat df = new SimpleDateFormat(pattern);
			String resultDate = df.format(date);
			return resultDate;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            转换规则
	 * @return
	 */
	public static Date dateFormatSTD(String date, String pattern) {
		if (common_util.isEmpty(date) || common_util.isEmpty(pattern)) {
			return null;
		}

		try {
			DateFormat df = new SimpleDateFormat(pattern);
			Date resultDate = df.parse(date);
			return resultDate;
		} catch (Exception e) {
			return null;
		}
	}

	// Blob转化成字符串
	public static String blob2string(Blob b, String encode) {
		String s = null;
		byte[] bs = null;
		try {
			if (b == null)
				return null;
			bs = blob2bytes(b);
			s = new String(bs, encode);
			return s;
		} catch (Exception ex) {
			return null;
		}
	}

	// 成字符串转化 Blob
	public static Blob string2blob(String str, String encode,
			LobHelper lobHelper) {
		try {
			byte[] bs = str.getBytes(encode);
			Blob blob = lobHelper.createBlob(bs);
			return blob;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Blob转化成byte[]
	public static byte[] blob2bytes(Blob b) {
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(b.getBinaryStream());
			byte[] bytes = new byte[(int) b.length()];
			int len = bytes.length;
			int offset = 0;
			int read = 0;
			while (offset < len
					&& (read = is.read(bytes, offset, len - offset)) >= 0) {
				offset += read;
			}
			return bytes;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				is.close();
				is = null;
			} catch (IOException e) {
				return null;
			}
		}
	}
}
