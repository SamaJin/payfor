package com.helphand.ccdms.commons;

import java.text.SimpleDateFormat;
import java.util.*;

public class common_converts {
	public String date_to_string(Date date, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		} catch (Exception e) {
			return null;
		}
	}

}
