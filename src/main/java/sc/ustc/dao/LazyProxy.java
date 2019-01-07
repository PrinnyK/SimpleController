package sc.ustc.dao;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import sc.ustc.bean.ClassBean;
import sc.ustc.bean.JDBCBean;
import sc.ustc.bean.PropertyBean;
import sc.ustc.util.DaoUtil;

// experiment, failure
public class LazyProxy implements MethodInterceptor{
	private JDBCBean jdbcBean;
	private ClassBean classBean;
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public JDBCBean getJdbcBean() {
		return jdbcBean;
	}

	public void setJdbcBean(JDBCBean jdbcBean) {
		this.jdbcBean = jdbcBean;
	}

	public ClassBean getClassBean() {
		return classBean;
	}

	public void setClassBean(ClassBean classBean) {
		this.classBean = classBean;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (method.getName().startsWith("get")) {
			Object result = proxy.invokeSuper(obj, args);
			if (null != result) {
				return result;
			} else {
				String propertyColumn = getPropertyColumn(method.getName());
				String table = classBean.getTable();
				String condition = "id=" + id;
				String preSql = "select %s from %s where %s";
				try (Connection connection = DriverManager.getConnection(jdbcBean.getUrl(), jdbcBean.getUserName(), jdbcBean.getUserPassword());
						Statement statement = connection.createStatement();){
					String sql = String.format(preSql, propertyColumn, table, condition);
					ResultSet resultSet = statement.executeQuery(sql);
					if (resultSet.next()) {
						DaoUtil.setPropertyValue(obj, getPropertyName(method.getName()), resultSet.getObject(propertyColumn), String.class);
						return resultSet.getObject(propertyColumn);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			return null;
		} else {
			return proxy.invokeSuper(obj, args);
		}
	}
	
	private String getPropertyName(String methodName) {
		char[] buf = methodName.substring(3, methodName.length()).toCharArray();
		buf[0] += 32;
		return String.valueOf(buf);
	}
	
	private String getPropertyColumn(String methodName) {
		for (PropertyBean propertyBean : classBean.getPropertyBeanList()) {
			if (methodName.substring(3, methodName.length()).equalsIgnoreCase(propertyBean.getName())) {
				return propertyBean.getColumn();
			}
		}
		return null;
	}
}