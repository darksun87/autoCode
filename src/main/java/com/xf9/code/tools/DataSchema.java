package com.xf9.code.tools;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.xf9.code.schema.ColumnSchema;
import com.xf9.code.schema.DatabaseSchema;
import com.xf9.code.schema.IndexSchema;
import com.xf9.code.schema.KeySchema;
import com.xf9.code.schema.TableSchema;

public class DataSchema {

	@Autowired
	public BasicDataSource datasource;

	public DatabaseSchema getDatabaseSchema() throws SQLException {

		Connection connection = null;
		try {
			// datasource =
			// ApplicationContextUtil.getBean(BasicDataSource.class);
			connection = datasource.getConnection();
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			DatabaseSchema databaseSchema = new DatabaseSchema();
			databaseSchema.setTables(getTables(databaseSchema));
			return databaseSchema;
		} finally {
			closeConnection(connection);
		}
	}

	public List<TableSchema> getTables(DatabaseSchema databaseSchema) throws SQLException {

		Connection connection = null;
		try {
			connection = datasource.getConnection();
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			String[] types = { "table" };
			ResultSet rs = databaseMetaData.getTables("", "", "", types);
			List<TableSchema> tableSchemas = new ArrayList<TableSchema>();
			while (rs.next()) {
				TableSchema tableSchema = new TableSchema();
				tableSchema.setTableCatalog(rs.getString("TABLE_CAT"));
				tableSchema.setTableName(rs.getString("TABLE_NAME"));
				tableSchema.setComment(rs.getString("REMARKS"));
				List<ColumnSchema> columnSchemas = getColumns(tableSchema);
				tableSchema.setColumns(columnSchemas);
				tableSchema.setIndexes(getIndexs(tableSchema));
				tableSchema.setPrimaryKey(getPrimaryKey(tableSchema));
				tableSchemas.add(tableSchema);
			}
			return tableSchemas;
		} finally {
			closeConnection(connection);
		}
	}

	public List<ColumnSchema> getColumns(TableSchema tableSchema) throws SQLException {
		Connection connection = null;
		try {
			connection = datasource.getConnection();

			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet rs = databaseMetaData.getColumns(tableSchema.getTableCatalog(), null,
					tableSchema.getTableName(), "%");
			List<ColumnSchema> columnSchemas = new ArrayList<ColumnSchema>();
			while (rs.next()) {
				String tableCat = rs.getString("TABLE_CAT");// 表目录（可能为空）
				String tableSchemaName = rs.getString("TABLE_SCHEM");// 表的架构（可能为空）
				String tableName_ = rs.getString("TABLE_NAME");// 表名
				String columnName = rs.getString("COLUMN_NAME");// 列名
				int dataType = rs.getInt("DATA_TYPE"); // 对应的java.sql.Types类型
				String dataTypeName = rs.getString("TYPE_NAME");// java.sql.Types类型
				int columnSize = rs.getInt("COLUMN_SIZE");// 列大小
				int decimalDigits = rs.getInt("DECIMAL_DIGITS");// 小数位数
				int nullAble = rs.getInt("NULLABLE");// 是否允许为null
				String isNullAble = rs.getString("IS_NULLABLE");
				String remarks = rs.getString("REMARKS");// 列描述
				String defaultValue = rs.getString("COLUMN_DEF");// 默认值
				int sqlDataType = rs.getInt("SQL_DATA_TYPE");// sql数据类型

				/**
				 * ISO规则用来确定某一列的为空性。 是---如果该参数可以包括空值; 无---如果参数不能包含空值
				 * 空字符串---如果参数为空性是未知的
				 */

				/**
				 * 指示此列是否是自动递增 是---如果该列是自动递增 无---如果不是自动递增列 空字串---如果不能确定它是否
				 * 列是自动递增的参数是未知
				 */
				String isAutoincrement = rs.getString("IS_AUTOINCREMENT");
				// String isGeneratedColumn =
				// rs.getString("IS_GENERATEDCOLUMN");

				ColumnSchema columnSchema = new ColumnSchema();
				columnSchema.setColumnName(columnName);
				columnSchema.setRemarks(remarks);
				columnSchema.setColumnSize(columnSize);
				columnSchema.setDataType(dataType);
				columnSchema.setDataTypeName(dataTypeName);
				columnSchema.setAutoIncrement(isAutoincrement.equalsIgnoreCase("YES"));
				columnSchema.setDecimalDigits(decimalDigits);
				columnSchema.setDefaultValue(defaultValue);
				columnSchema.setNullable(nullAble == 1);
				columnSchemas.add(columnSchema);
			}
			return columnSchemas;
		} finally {
			closeConnection(connection);
		}
	}

	public KeySchema getPrimaryKey(TableSchema tableSchema) throws SQLException {
		Connection connection = null;
		try {
			connection = datasource.getConnection();
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet rs = databaseMetaData.getPrimaryKeys(tableSchema.getTableCatalog(), null, tableSchema.getTableName());
			KeySchema keySchema = new KeySchema();
			List<ColumnSchema> memberColumns = new ArrayList<ColumnSchema>();
			keySchema.setMemberColumns(memberColumns);
			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");// 列名
				for (ColumnSchema column : tableSchema.getColumns()) {
					if (columnName.equalsIgnoreCase(column.getColumnName())) {
						column.setPrimary(true);
						tableSchema.setPrimaryColumn(column);
						memberColumns.add(column);
						break;
					}
				}
				String keyName = rs.getString("PK_NAME"); // 对应的java.sql.Types类型
				keySchema.setKeyName(keyName);
			}
			return keySchema;
		} finally {
			closeConnection(connection);
		}
	}

	public List<IndexSchema> getIndexs(TableSchema tableSchema) throws SQLException {
		Connection connection = null;
		try {
			connection = datasource.getConnection();
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet rs = databaseMetaData.getIndexInfo(tableSchema.getTableCatalog(), null, tableSchema.getTableName(), false, true);

			List<IndexSchema> indexSchemas = new ArrayList<IndexSchema>();
			Map<String, IndexSchema> indexColumns = new HashMap<String, IndexSchema>();
			// List<ColumnSchema> memberColumns = new ArrayList<ColumnSchema>();
			// keySchema.setMemberColumns(memberColumns);
			addIndex(tableSchema, rs, indexColumns, indexSchemas);
			// rs = databaseMetaData.getIndexInfo(tableSchema.getTableCatalog(),
			// null, tableSchema.getTableName(), false, true);
			// addIndex(tableSchema, rs, indexColumns, indexSchemas);
			return indexSchemas;
		} finally {
			closeConnection(connection);
		}
	}

	private void addIndex(TableSchema tableSchema, ResultSet rs, Map<String, IndexSchema> indexColumns, List<IndexSchema> indexSchemas) throws SQLException {
		while (rs.next()) {
			String indexName = rs.getString("INDEX_NAME");// 索引的名称
			if (indexName.equalsIgnoreCase("PRIMARY")) {
				continue;
			}
			short type = rs.getShort("TYPE");// 索引类型
			boolean isUnique = !rs.getBoolean("NON_UNIQUE");// 索引的名称
			String columnName = rs.getString("COLUMN_NAME");// 列名
			String ascOrDesc = rs.getString("ASC_OR_DESC");// 列排序顺序:升序还是降序
			IndexSchema indexSchema = null;
			if (!indexColumns.containsKey(indexName)) {
				indexSchema = new IndexSchema();
				indexSchema.setIndexName(indexName);
				indexSchema.setUnique(isUnique);
				indexSchema.setMemberColumns(new ArrayList<ColumnSchema>());
				indexSchemas.add(indexSchema);
				indexColumns.put(indexName, indexSchema);
			}
			else {
				indexSchema = indexColumns.get(indexName);
			}
			for (ColumnSchema column : tableSchema.getColumns()) {
				if (columnName.equalsIgnoreCase(column.getColumnName())) {
					indexSchema.getMemberColumns().add(column);
					break;
				}
			}
		}
	}

	private void closeConnection(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
