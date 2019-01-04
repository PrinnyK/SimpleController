package sc.ustc.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sc.ustc.bean.ClassBean;
import sc.ustc.bean.PropertyBean;
import sc.ustc.util.DaoUtil;

public class Conversation {
	
	public static <T> Boolean insertObject(Connection connection, T obj, Class<T> clazz) {
		// build sql
		String sql = "insert into ";
		String className = clazz.getSimpleName();
		ClassBean classBean = Configuration.getClassInfo(className);

		String table = classBean.getTable();
		sql = sql + table + " ";
		
		List<String> columns = new ArrayList<>();
		List<String> values = new ArrayList<>();
		for (PropertyBean propertyBean : classBean.getPropertyBeanList()) {
			Object propertyValue = DaoUtil.getPropertyValue(obj, clazz, propertyBean.getName());
			if (null != propertyValue) {
				columns.add(propertyBean.getColumn());
				values.add("'" + (String)propertyValue + "'");
			}
		}
		sql = sql + "(" + String.join(", ", columns) + ") values (" + String.join(", ", values) + ")";

		// get resultSet and build bean
		try (Statement stat = connection.createStatement();) {
			stat.execute(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public static <T> T getObject(Connection connection, T obj, Class<T> clazz) {
		// build sql
		String sql = "select * from ";
		String condition = "where ";
		String className = clazz.getSimpleName();
		ClassBean classBean = Configuration.getClassInfo(className);

		String table = classBean.getTable();
		sql = sql + table + " ";

		String idName = classBean.getIdName();
		int id = (int) DaoUtil.getPropertyValue(obj, clazz, idName);
		if (id > 0) {
			condition = condition + "id = " + id;
		}

		for (PropertyBean propertyBean : classBean.getPropertyBeanList()) {
			Object propertyValue = DaoUtil.getPropertyValue(obj, clazz, propertyBean.getName());
			if (null != propertyValue) {
				if (condition.contains("=")) {
					condition += " and ";
				}
				condition = condition + propertyBean.getColumn() + " = '" + (String) propertyValue + "'";
			}
		}
		sql += condition;

		// get resultSet and build bean
		try (Statement stat = connection.createStatement();) {
			ResultSet resultSet = stat.executeQuery(sql);

			if (resultSet.next()) {
				T result = clazz.getConstructor().newInstance();
				Object resultId = resultSet.getObject("id");
				Class<?> resultIdType = DaoUtil.getType("int");
				DaoUtil.setPropertyValue(result, clazz, classBean.getIdName(), resultId, resultIdType);

				for (PropertyBean propertyBean : classBean.getPropertyBeanList()) {
					Object resultValue = resultSet.getObject(propertyBean.getColumn());
					Class<?> resultValueType = DaoUtil.getType(propertyBean.getType());
					DaoUtil.setPropertyValue(result, clazz, propertyBean.getName(), resultValue, resultValueType);
				}

				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}
	
	public static <T> Boolean updateObject(Connection connection, T obj, Class<T> clazz) {
		// build sql
		String sql = "update ";
		String className = clazz.getSimpleName();
		ClassBean classBean = Configuration.getClassInfo(className);

		String table = classBean.getTable();
		sql = sql + table + " ";

		List<String> columns = new ArrayList<>();
		for (PropertyBean propertyBean : classBean.getPropertyBeanList()) {
			Object propertyValue = DaoUtil.getPropertyValue(obj, clazz, propertyBean.getName());
			if (null != propertyValue) {
				columns.add(propertyBean.getColumn() + " = '" + (String)propertyValue + "'");
			}
		}
		
		String condition = " where ";
		int id = (int) DaoUtil.getPropertyValue(obj, clazz, classBean.getIdName());
		if (id > 0) {
			condition = condition + "id = " + id;
		}
		
		sql = sql + "set " + String.join(", ", columns) + condition;

		// get resultSet and build bean
		try (Statement stat = connection.createStatement();) {
			stat.execute(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public static <T> Boolean deleteObject(Connection connection, T obj, Class<T> clazz) {
		// build sql
		String sql = "delete from ";
		String className = clazz.getSimpleName();
		ClassBean classBean = Configuration.getClassInfo(className);

		String table = classBean.getTable();
		sql = sql + table + " ";

		String condition = "where ";
		int id = (int) DaoUtil.getPropertyValue(obj, clazz, classBean.getIdName());
		if (id > 0) {
			condition = condition + "id = " + id;
		}

		sql = sql + condition;

		// get resultSet and build bean
		try (Statement stat = connection.createStatement();) {
			stat.execute(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
}
