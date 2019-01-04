package sc.ustc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO {
	protected String driver;
	protected String url;
	protected String userName;
	protected String userPassword;
	
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	public Connection openDBConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, userName, userPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	public boolean closeDBConnection(Connection conn) {
		boolean result = true;
		if (null != conn) {
			try {
				conn.close();
			} catch (SQLException e) {
				result = false;
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public abstract <T> T query(T obj, Class<T> clazz);
	public abstract <T> boolean insert(T obj, Class<T> clazz);
	public abstract <T> boolean update(T obj, Class<T> clazz);
	public abstract <T> boolean delete (T obj, Class<T> clazz);
}
