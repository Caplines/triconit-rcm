package com.tricon.rcm.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class Util {

	// only for Predefine classes
	public static boolean checkNullOrEmpty(Object obj) {
		boolean isNull = false;

		if (obj == null) {
			isNull = true;
		}

		if (obj instanceof List) {
			List<?> list = (List<?>) obj;
			if (list.isEmpty() || list.size() == 0)
				isNull = true;
		}
		if (obj instanceof Set) {
			Set<?> set = (Set<?>) obj;
			if (set.isEmpty() || set.size() == 0)
				isNull = true;
		}
		if (obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) obj;
			if (map.isEmpty() || map.size() == 0)
				isNull = true;
		}

		if (obj instanceof String) {
			String string = (String) obj;
			if (!StringUtils.isNoneBlank(string)) {
				isNull = true;
			}
		}
		if (obj instanceof StringBuffer) {
			StringBuffer string = (StringBuffer) obj;
			if (!StringUtils.isNoneBlank(string)) {
				isNull = true;
			}
		}
		if (obj instanceof StringBuilder) {
			StringBuilder string = (StringBuilder) obj;
			if (!StringUtils.isNoneBlank(string)) {
				isNull = true;
			}
		}
		return isNull;
	}
}
