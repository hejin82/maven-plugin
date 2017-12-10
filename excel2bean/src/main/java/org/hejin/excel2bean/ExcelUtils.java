package org.hejin.excel2bean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

	private static List<String> fieldList = new ArrayList<>();
	private static Map<String, String> fieldMapped = new  HashMap<>();

	public static<T> List<T> xls2Obj(InputStream xls,Class<T> classz) {
		return xls2Obj(xls,classz,null);
	}

	private static<T> List<T> xls2Obj(InputStream xls, Class<T> classz, Object object) {
		return null;
	}
	
}
