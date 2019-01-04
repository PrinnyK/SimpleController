package sc.ustc.dao;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import sc.ustc.bean.ClassBean;
import sc.ustc.bean.JDBCBean;
import sc.ustc.bean.PropertyBean;
import sc.ustc.util.XMLUtil;

public class Configuration {
	private static String path = Thread.currentThread().getContextClassLoader().getResource("/or_mapping.xml").getPath();
	
	public static JDBCBean getJDBCconfig() {
		Document document = XMLUtil.getXMLDocument(path);
		Element rootElement = document.getRootElement();
		Element jdbcElement = rootElement.element("jdbc");
		List<Element> jdbcProperties = jdbcElement.elements("property");
		
		JDBCBean result = new JDBCBean();
		result.setDriver(XMLUtil.getPropertyValueFromList(jdbcProperties, "driver_class"));
		result.setUrl(XMLUtil.getPropertyValueFromList(jdbcProperties, "url_path"));
		result.setUserName(XMLUtil.getPropertyValueFromList(jdbcProperties, "db_username"));
		result.setUserPassword(XMLUtil.getPropertyValueFromList(jdbcProperties, "db_userpassword"));
		
		return result;
	}
	
	public static ClassBean getClassInfo(String className) {
		Document document = XMLUtil.getXMLDocument(path);
		Element rootElement = document.getRootElement();
		List<Element> classElementList = rootElement.elements("class");
		
		// find the correct element in class list
		Element classElement = null;
		for (Element element : classElementList) {
			if (className.equals(element.elementText("name"))) {
				classElement = element;
			}
		}
		
		ClassBean result = new ClassBean();
		result.setName(classElement.elementText("name"));
		result.setTable(classElement.elementText("table"));
		result.setIdName(classElement.element("id").elementText("name"));
		
		List<PropertyBean> propertyBeanList = new ArrayList<>();
		for (Object element : classElement.elements("property")) {
			PropertyBean propertyBean = new PropertyBean();
			propertyBean.setName(((Element)element).elementText("name"));
			propertyBean.setColumn(((Element)element).elementText("column"));
			propertyBean.setType(((Element)element).elementText("type"));
			propertyBean.setLazy(((Element)element).elementText("lazy"));
			
			propertyBeanList.add(propertyBean);
		}
		result.setPropertyBeanList(propertyBeanList);
		
		return result;
	}
	
	
}
