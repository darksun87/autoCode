package com.xf9.code.util;

import org.springframework.util.StringUtils;

import com.xf9.code.schema.TableSchema;

public class NameUtil {

	public static String getMapperClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName() + "Mapper";
	}

	public static String getMapperVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getMapperClassName(tableSchema));
	}

	public static String getFullMapperClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".dao."
				+ getMapperClassName(tableSchema);
	}

	public static String getModelVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getModelClassName(tableSchema));
	}

	public static String getModelClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName();
	}

	public static String getFullModelClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".model."
				+ getModelClassName(tableSchema);
	}

	public static String getModelQueryClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName() + "Query";
	}

	public static String getModelQueryVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getModelQueryClassName(tableSchema));
	}

	public static String getFullModelQueryClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".model."
				+ getModelQueryClassName(tableSchema);
	}

	public static String getConvertorClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName() + "Convertor";
	}

	public static String getConvertorVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getConvertorClassName(tableSchema));
	}

	public static String getFullConvertorClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".service.convertor."
				+ getConvertorClassName(tableSchema);
	}

	public static String getMessageQueryClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName() + "QueryInfo";
	}

	public static String getMessageQueryVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getMessageQueryClassName(tableSchema));
	}

	public static String getFullMessageQueryClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".message."
				+ getMessageQueryClassName(tableSchema);
	}

	public static String getBizClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName() + "Biz";
	}

	public static String getBizVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getBizClassName(tableSchema));
	}

	public static String getFullBizClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".biz."
				+ getBizClassName(tableSchema);
	}

	public static String getMessageClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName() + "Info";
	}

	public static String getMessageVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getMessageClassName(tableSchema));
	}

	public static String getFullMessageClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".message."
				+ getMessageClassName(tableSchema);
	}

	public static String getServiceClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName() + "Service";
	}

	public static String getServiceVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getServiceClassName(tableSchema));
	}

	public static String getFullServiceClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".service."
				+ getServiceClassName(tableSchema);
	}

	public static String getServiceImplClassName(TableSchema tableSchema)
	{
		return getServiceClassName(tableSchema) + "Impl";
	}

	public static String getFullServiceImplClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".service.impl."
				+ getServiceImplClassName(tableSchema);
	}

	// ---
	public static String getControllerClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName() + "Controller";
	}

	public static String getControllerVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getControllerClassName(tableSchema));
	}

	public static String getFullControllerClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".service.controller."
				+ getControllerClassName(tableSchema);
	}
	// --
	public static String getTestClassName(TableSchema tableSchema)
	{
		return tableSchema.getModelName() + "Test";
	}

	public static String getTestVarName(TableSchema tableSchema)
	{
		return StringUtils.uncapitalize(getTestClassName(tableSchema));
	}

	public static String getFullTestClassName(TableSchema tableSchema)
	{
		return PropertyPlaceholder.getProperty("basePackage")
				+ ".service.test."
				+ getTestClassName(tableSchema);
	}

}
