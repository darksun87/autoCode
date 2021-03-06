package com.xf9.code.schema;

import java.util.List;

import org.springframework.util.StringUtils;

import com.xf9.code.util.PropertyPlaceholder;
import com.xf9.code.util.StringUtil;

public class TableSchema {

	public static final String UPDATE_USERID_COLUMN = "update_userid";

	public static final String UPDATE_TIME_COLUMN = "update_time";

	public static final String CREATE_USERID_COLUMN = "create_userid";

	public static final String CREATE_TIME_COLUMN = "create_time";

	public static final String DELETED_COLUMN = "is_deleted";

	public static final String STATUS_COLUMN = "status";

	private String tableCatalog;

	private String tableName;

	private String comment;

	private String modelName;

	private ColumnSchema primaryColumn;

	private ColumnSchema statusColumn;

	private KeySchema primaryKey;

	private List<IndexSchema> indexes;

	public String getDisplayName()
	{
		return StringUtils.hasText(this.comment) ? this.comment : this.getModelName();
	}

	public String getTableName() {
		return tableName;
	}

	public boolean hasStatusColumn()
	{
		for (ColumnSchema columnSchema : columns) {
			if (columnSchema.isStatusColumn()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasCreateColumn()
	{
		for (ColumnSchema columnSchema : columns) {
			if (columnSchema.getColumnName().equalsIgnoreCase("create_time")) {
				return true;
			}
		}
		return false;
	}

	public boolean hasUpdateColumn()
	{
		for (ColumnSchema columnSchema : columns) {
			if (columnSchema.getColumnName().equalsIgnoreCase("update_time")) {
				return true;
			}
		}
		return false;
	}

	public boolean hasDeleteColumn()
	{
		for (ColumnSchema columnSchema : columns) {
			if (columnSchema.getColumnName().equalsIgnoreCase("is_deleted")) {
				return true;
			}
		}
		return false;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
		this.setModelName(getModelName());
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;

	}

	private List<ColumnSchema> columns;

	public List<ColumnSchema> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnSchema> columns) {
		this.columns = columns;
	}

	public String getTableCatalog() {
		return tableCatalog;
	}

	public void setTableCatalog(String tableCatalog) {
		this.tableCatalog = tableCatalog;
	}

	public KeySchema getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(KeySchema primaryKey) {
		this.primaryKey = primaryKey;
	}

	public ColumnSchema getPrimaryColumn() {
		return primaryColumn;
	}

	public void setPrimaryColumn(ColumnSchema primaryColumn) {
		this.primaryColumn = primaryColumn;
	}

	public List<IndexSchema> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<IndexSchema> indexes) {
		this.indexes = indexes;
	}

	public String getModelName() {
		String prefix = PropertyPlaceholder.getProperty("prefix").toString();

		String name = this.getTableName();
		if (StringUtil.isNotBlank(prefix)) {
			if (name.startsWith(prefix)) {
				name = name.substring((prefix.length() - 1));
			}
		}
		String[] ss = name.split("\\_");
		StringBuilder sb = new StringBuilder();
		for (String s : ss) {
			sb.append(StringUtils.capitalize(s));
		}
		return sb.toString();

	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public ColumnSchema getStatusColumn() {
		if (statusColumn == null) {
			for (ColumnSchema columnSchema : columns) {
				if (columnSchema.getColumnName().equalsIgnoreCase("status")) {
					statusColumn = columnSchema;
					break;
				}
			}
		}
		return statusColumn;
	}

	public void setStatusColumn(ColumnSchema statusColumn) {
		this.statusColumn = statusColumn;
	}

	public boolean hasUniqueIndex()
	{
		for (IndexSchema indexSchema : indexes) {
			if (indexSchema.isUnique()) {
				return true;
			}
		}
		return false;
	}

	public ColumnSchema getCreateUserIdColumn() {
		for (ColumnSchema columnSchema : columns) {
			if (columnSchema.isCreateUseridColumn()) {
				return columnSchema;
			}
		}
		return null;
	}

	public ColumnSchema getCreateTimeColumn() {
		for (ColumnSchema columnSchema : columns) {
			if (columnSchema.isCreateTimeColumn()) {
				return columnSchema;
			}
		}
		return null;
	}

	public ColumnSchema getUpdateUserIdColumn() {
		for (ColumnSchema columnSchema : columns) {
			if (columnSchema.isUpdateUseridColumn()) {
				return columnSchema;
			}
		}
		return null;
	}

	public ColumnSchema getUpdateTimeColumn() {
		for (ColumnSchema columnSchema : columns) {
			if (columnSchema.isUpdateTimeColumn()) {
				return columnSchema;
			}
		}
		return null;
	}
}
