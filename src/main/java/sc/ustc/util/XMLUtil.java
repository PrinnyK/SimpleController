package sc.ustc.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class XMLUtil {
	public static Document getXMLDocument(String XMLPath) {
		
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(new File(XMLPath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return document;
	}
	
	public static Element getTargetElementFromList(List<Element> src, String targetName) {
		Element result = null;
		for (Element element : src) {
			if (targetName.equals(element.attributeValue("name"))) {
				result = element;
				break;
			}
		}
		return result;
	}
	
	public static String getPropertyValueFromList(List<Element> src, String targetName) {
		for (Element element : src) {
			if (targetName.equals(element.elementText("name"))) {
				return element.elementText("value");
			}
		}
		return null;
	}
	
	public static ByteArrayOutputStream XMLtoHTML(String xsl_path,String xml_path){
		ByteArrayOutputStream result = null;
		try {
			StreamSource source_xsl = new StreamSource(
					Thread.currentThread().getContextClassLoader().getResourceAsStream(xsl_path));
			StreamSource source_xml = new StreamSource(
					Thread.currentThread().getContextClassLoader().getResourceAsStream(xml_path));

			result = new ByteArrayOutputStream();
			Transformer transformer = TransformerFactory.newInstance().newTransformer(source_xsl);
			transformer.transform(source_xml, new StreamResult(result));
        } catch (Exception e) {
			// TODO: handle exception
		}
		
        return result;
	}
	
	public static String getFormatTime() {
		SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return pattern.format(date);
	}
}
