package com.xf9.code.util;

import java.util.List;

import com.xf9.code.schema.ColumnSchema;

public class CommonUtil {
	public static boolean isLastColumn(List<ColumnSchema> columns, ColumnSchema col) {
		return columns.indexOf(col) == (columns.size() - 1);
	}

	public static boolean isFirstColumn(List<ColumnSchema> columns, ColumnSchema col) {
		return columns.indexOf(col) == 0;
	}
}
