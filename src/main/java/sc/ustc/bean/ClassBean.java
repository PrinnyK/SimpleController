package sc.ustc.bean;

import java.util.List;

public class ClassBean {
	private String name;
	private String table;
	private String idName;
	private List<PropertyBean> propertyBeanList;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getIdName() {
		return idName;
	}
	public void setIdName(String idName) {
		this.idName = idName;
	}
	public List<PropertyBean> getPropertyBeanList() {
		return propertyBeanList;
	}
	public void setPropertyBeanList(List<PropertyBean> propertyBeanList) {
		this.propertyBeanList = propertyBeanList;
	}
}
