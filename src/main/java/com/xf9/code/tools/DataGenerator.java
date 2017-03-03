package com.xf9.code.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolInfo;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.Toolbox;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xf9.code.schema.ColumnSchema;
import com.xf9.code.schema.DatabaseSchema;
import com.xf9.code.schema.TableSchema;
import com.xf9.code.util.CodeUtil;
import com.xf9.code.util.NameUtil;
import com.xf9.code.util.PropertyPlaceholder;
import com.xf9.code.util.SqlUtil;
import com.xf9.code.util.StringUtil;

public class DataGenerator {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = null;
		try {
			applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			DataSchema dataSchema = applicationContext.getBean("dataSchema", DataSchema.class);
			DatabaseSchema databaseSchema = dataSchema.getDatabaseSchema();
			// System.out.print(JSON.toJSONString(databaseSchema));
			generateCode(databaseSchema);
			// generateTableDict(applicationContext, databaseSchema);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			applicationContext.close();
		}
	}

	private static void generateCode(DatabaseSchema databaseSchema) throws IOException {
		String[] tables = PropertyPlaceholder.getProperty("tables").toString().split("\\,");
		String rootDir = PropertyPlaceholder.getProperty("output.root.dir").toString();
		deleteSubFiles(new File(rootDir));
		for (TableSchema tableSchema : databaseSchema.getTables()) {
			for (String tableName : tables) {
				if (tableSchema.getTableName().equalsIgnoreCase(tableName)) {
					generate(tableSchema, rootDir);
				}
			}
		}
	}

	private static void generateTableDict(ApplicationContext applicationContext, DatabaseSchema databaseSchema) {

		BasicDataSource registryDatasource = applicationContext.getBean("registryDatasource", BasicDataSource.class);
		java.sql.Connection connection = null;
		try {
			connection = registryDatasource.getConnection();
			PreparedStatement queryStatement = connection.prepareStatement("SELECT 1 from service_dict_head WHERE db_name=? and table_name=? and filed_name=?");
			PreparedStatement insertStatement = connection
					.prepareStatement(
					"INSERT INTO service_dict_head"
							+ "(db_name,table_name,filed_name,dataType,length,is_key,is_null,default_value,COMMENT,create_userid,create_time,update_userid,update_time,property_name)"
							+ "VALUE(?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			for (TableSchema tableSchema : databaseSchema.getTables()) {

				for (ColumnSchema columnSchema : tableSchema.getColumns()) {
					queryStatement.setString(1, tableSchema.getTableCatalog());
					queryStatement.setString(2, tableSchema.getTableName());
					queryStatement.setString(3, columnSchema.getColumnName());

					ResultSet rs = queryStatement.executeQuery();
					if (rs.next()) {
						continue;
					}
					insertStatement.setString(1, tableSchema.getTableCatalog());
					insertStatement.setString(2, tableSchema.getTableName());
					insertStatement.setString(3, columnSchema.getColumnName());
					insertStatement.setString(4, columnSchema.getDataTypeName());
					insertStatement.setDouble(5, columnSchema.getColumnLength());
					insertStatement.setBoolean(6, columnSchema.isPrimary());
					insertStatement.setBoolean(7, columnSchema.isNotNull());
					insertStatement.setString(8, columnSchema.getDefaultValue());
					insertStatement.setString(9, columnSchema.getDisplayName());
					insertStatement.setString(10, "system");
					insertStatement.setLong(11, System.currentTimeMillis());
					insertStatement.setString(12, "system");
					insertStatement.setLong(13, System.currentTimeMillis());
					insertStatement.setString(14, columnSchema.getPropertyName());
					insertStatement.execute();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}

		}

	}

	public static void generate(TableSchema tableSchema, String rootDir) throws IOException {

		Velocity.init();
		ToolManager manager = new ToolManager();
		// manager.
		ToolContext ctx = manager.createContext();
		addToolBox(ctx);
		ctx.put("table", tableSchema);

		 generateViewAndController(tableSchema, ctx, rootDir);

		 generate(
		 "/src/main/resources/templates/index_edit.vm",
		 rootDir + "views\\" + NameUtil.getModelVarName(tableSchema) +
		 "\\edit.html",
		 tableSchema,
		 ctx);

		generateCode(tableSchema, ctx, rootDir);
	}

	private static void generateViewAndController(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/index_view.vm",
				rootDir + "views\\" + NameUtil.getModelVarName(tableSchema) +
						"\\index.html",
				tableSchema,
				ctx);
		generate(
				"/src/main/resources/templates/controller.vm",
				rootDir + "controller\\" +
						NameUtil.getModelClassName(tableSchema) + "Controller.java",
				tableSchema,
				ctx);
	}

	private static void generateCode(TableSchema tableSchema, ToolContext ctx, String rootDir) throws IOException {
		generate(
				"/src/main/resources/templates/biz.vm",
				rootDir + "biz\\" + NameUtil.getBizClassName(tableSchema) + ".java",
				tableSchema,
				ctx);

		generate(
				"/src/main/resources/templates/mapper_xml.vm",
				rootDir + "mappers\\" + NameUtil.getModelClassName(tableSchema) + ".xml",
				tableSchema,
				ctx);

		generate(
				"/src/main/resources/templates/mapper.vm",
				rootDir + "dao\\" + NameUtil.getMapperClassName(tableSchema) + ".java",
				tableSchema,
				ctx);

		generate(
				"/src/main/resources/templates/model.vm",
				rootDir + "model\\" + NameUtil.getModelClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
		generate(
				"/src/main/resources/templates/modelQuery.vm",
				rootDir + "model\\" + NameUtil.getModelQueryClassName(tableSchema) + ".java",
				tableSchema,
				ctx);

		generate(
				"/src/main/resources/templates/service.vm",
				rootDir + "service\\" + NameUtil.getServiceClassName(tableSchema) + ".java",
				tableSchema,
				ctx);

		generate(
				"/src/main/resources/templates/message.vm",
				rootDir + "message\\" + NameUtil.getMessageClassName(tableSchema) + ".java",
				tableSchema,
				ctx);

		generate(
				"/src/main/resources/templates/messageQuery.vm",
				rootDir + "message\\" + NameUtil.getMessageQueryClassName(tableSchema) + ".java",
				tableSchema,
				ctx);

		generate(
				"/src/main/resources/templates/service_impl.vm",
				rootDir + "service_impl\\" + NameUtil.getServiceImplClassName(tableSchema) + ".java",
				tableSchema,
				ctx);

		generate(
				"/src/main/resources/templates/convertor.vm",
				rootDir + "service_convertor\\" + NameUtil.getConvertorClassName(tableSchema) + ".java",
				tableSchema,
				ctx);

		generate(
				"/src/main/resources/templates/service_test.vm",
				rootDir + "tests\\" + NameUtil.getTestClassName(tableSchema) + ".java",
				tableSchema,
				ctx);
	}

	private static void generate(
			String templateName,
			String fileName,
			TableSchema tableSchema,
			ToolContext ctx) throws IOException {
		Template template = Velocity.getTemplate(templateName, "utf-8");
		StringWriter writer = new StringWriter();
		template.merge(ctx, writer);
		PrintWriter filewriter = null;
		FileOutputStream fileOutputStream = null;
		try {
			File file = new File(fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fileOutputStream = new FileOutputStream(fileName);
			filewriter = new PrintWriter(fileOutputStream, true);
			filewriter.print(writer.toString());
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally
		{
			filewriter.close();
			fileOutputStream.close();
		}
	}

	private static void deleteSubFiles(File parentFile) {
		if (!parentFile.isDirectory()) {
			return;
		}
		String[] subFiles = parentFile.list();
		for (String pathname : subFiles) {
			File file = new File(parentFile, pathname);
			if (file.isDirectory()) {
				deleteSubFiles(file);
			}
			file.delete();
		}
	}

	private static void addToolBox(ToolContext ctx) {
		Map<String, ToolInfo> toolMap = new HashMap<String, ToolInfo>();
		toolMap.put("config", new ToolInfo("config", PropertyPlaceholder.class));
		toolMap.put("sql", new ToolInfo("sql", SqlUtil.class));
		toolMap.put("stringUtil", new ToolInfo("stringUtil", StringUtil.class));
		toolMap.put("name", new ToolInfo("name", NameUtil.class));
		toolMap.put("code", new ToolInfo("code", CodeUtil.class));
		Toolbox toolbox = new Toolbox(toolMap);
		ctx.addToolbox(toolbox);
	}
}
