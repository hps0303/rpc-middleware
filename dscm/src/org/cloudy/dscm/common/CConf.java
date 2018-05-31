package org.cloudy.dscm.common;

import java.nio.charset.Charset;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

public class CConf {
	public static final String DF = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String CTXNAME = "___ctx___";
	public static final String ACTN = "actn";
	public static final String CLAZ = "claz";
	public static final String SYNC = "sync";
	public static final String SAFE = "safe";
	public static final String ERRS = "errs";
	public static final String UTF8 = "UTF-8";
	public static final String UUID = "__i__";
	public static final Charset CUTF = Charset.forName("UTF-8");

	public static final SerializerFeature[] FEATURE = { SerializerFeature.WriteClassName, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.IgnoreNonFieldGetter };

	public static Class<?> clazz(String name) {
		if (name.equals("int")) {
			return Integer.TYPE;
		}

		if (name.equals("long")) {
			return Long.TYPE;
		}

		if (name.equals("float")) {
			return Float.TYPE;
		}

		if (name.equals("double")) {
			return Double.TYPE;
		}

		if (name.equals("byte")) {
			return Byte.TYPE;
		}

		if (name.equals("boolean")) {
			return Boolean.TYPE;
		}

		if (name.equals("char")) {
			return Character.TYPE;
		}

		if (name.equals("short")) {
			return Short.TYPE;
		}

		if (name.equals("void")) {
			return Void.class;
		}
		return null;
	}

	public static int toInt(String str) {
		return Integer.parseInt(str);
	}

	public static int toInt(String str, int def) {
		if (str == null) {
			return def;
		}
		return Integer.parseInt(str);
	}

	public static long toLong(String str) {
		return Long.parseLong(str);
	}

	public static long toLong(String str, long def) {
		if (str == null) {
			return def > 0L ? def : 0L;
		}
		return Long.parseLong(str);
	}

	public static boolean toBool(String str) {
		return Boolean.valueOf(str).booleanValue();
	}

	static {
		SerializeConfig.getGlobalInstance().put(java.util.Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss.SSS"));
		SerializeConfig.getGlobalInstance().put(java.sql.Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss.SSS"));
	}
}
