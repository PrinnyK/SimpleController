package sc.ustc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import sc.ustc.bean.ClassBean;
import sc.ustc.bean.PropertyBean;
import sc.ustc.util.DaoUtil;

public class Conversation {
	
	public static <T> Boolean insertObject(Connection connection, T obj) {
		// build sql
		String sql = "insert into ";
		Class<T> clazz = (Class<T>) obj.getClass();
		String className = clazz.getSimpleName();
		ClassBean classBean = Configuration.getClassInfo(className);

		String table = classBean.getTable();
		sql = sql + table + " ";
		
		List<String> columns = new ArrayList<>();
		List<String> values = new ArrayList<>();
		for (PropertyBean propertyBean : classBean.getPropertyBeanList()) {
			Object propertyValue = DaoUtil.getPropertyValue(obj, propertyBean.getName());
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
	
	public static <T> T getObject(Connection connection, T obj) {
		// get class bean
		Class<T> clazz = (Class<T>) obj.getClass();
		String className = clazz.getSimpleName();
		ClassBean classBean = Configuration.getClassInfo(className);

		// get table
		String table = classBean.getTable();

		// get condition and non-lazy property
		String condition = "";
		String idName = classBean.getIdName();
		int id = (int) DaoUtil.getPropertyValue(obj, idName);
		if (id > 0) {
			condition = condition + "id = " + id;
		}
		List<String> properties = new ArrayList<>();
		properties.add("id");
		for (PropertyBean propertyBean : classBean.getPropertyBeanList()) {
			if (propertyBean.getLazy().equalsIgnoreCase("false")) {
				properties.add(propertyBean.getColumn());
			}
			Object propertyValue = DaoUtil.getPropertyValue(obj, propertyBean.getName());
			if (null != propertyValue) {
				if (condition.contains("=")) {
					condition += " and ";
				}
				condition = condition + propertyBean.getColumn() + " = '" + (String) propertyValue + "'";
			}
		}
		String property = String.join(", ", properties); 
		
		String preSql = "select %s from %s where %s";
		try (PreparedStatement pstat = connection.prepareStatement(preSql);
				Statement statement = connection.createStatement();) {
			String sql = String.format(preSql, property, table, condition);
			ResultSet resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				// set non-lazy property
				Object resultId = resultSet.getObject("id");
				LazyProxy lazyProxy = new LazyProxy();
				lazyProxy.setClassBean(classBean);
				lazyProxy.setJdbcBean(Configuration.getJDBCconfig());
				lazyProxy.setId((int)resultId);
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(clazz);
				enhancer.setCallback(lazyProxy);
				T result = (T) enhancer.create();
				
				DaoUtil.setPropertyValue(result, classBean.getIdName(), resultId, int.class);
				for (PropertyBean propertyBean : classBean.getPropertyBeanList()) {
					if (propertyBean.getLazy().equalsIgnoreCase("false")) {
						Object resultValue = resultSet.getObject(propertyBean.getColumn());
						DaoUtil.setPropertyValue(result, propertyBean.getName(), resultValue, resultValue.getClass());
					}
				}

				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}
	
	public static <T> Boolean updateObject(Connection connection, T obj) {
		// build sql
		String sql = "update ";
		Class<T> clazz = (Class<T>) obj.getClass();
		String className = clazz.getSimpleName();
		ClassBean classBean = Configuration.getClassInfo(className);

		String table = classBean.getTable();
		sql = sql + table + " ";

		List<String> columns = new ArrayList<>();
		for (PropertyBean propertyBean : classBean.getPropertyBeanList()) {
			Object propertyValue = DaoUtil.getPropertyValue(obj, propertyBean.getName());
			if (null != propertyValue) {
				columns.add(propertyBean.getColumn() + " = '" + (String)propertyValue + "'");
			}
		}
		
		String condition = " where ";
		int id = (int) DaoUtil.getPropertyValue(obj, classBean.getIdName());
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
	
	public static <T> Boolean deleteObject(Connection connection, T obj) {
		// build sql
		String sql = "delete from ";
		Class<T> clazz = (Class<T>) obj.getClass();
		String className = clazz.getSimpleName();
		ClassBean classBean = Configuration.getClassInfo(className);

		String table = classBean.getTable();
		sql = sql + table + " ";

		String condition = "where ";
		int id = (int) DaoUtil.getPropertyValue(obj, classBean.getIdName());
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
