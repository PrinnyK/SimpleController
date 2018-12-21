package sc.ustc.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class SimpleController extends HttpServlet{
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// if http://host/UseSC/login.sc, then split get "", "UseSc", "login.sc"
		String[] reqURI = req.getRequestURI().split("/");
		String action = reqURI[2].substring(0, reqURI[2].lastIndexOf("."));
		
		// get action node
		Element targetElement = getTargetElement(action);
		
		// get action class name and method name
		List<String> actionAttrs = getActionAttrs(targetElement, action);
		
		// if match hit
		if (null != actionAttrs) {
			try {
				
				// pass class name and method name to get result by reflection
				String result = getResult(actionAttrs, req, resp);
				
				// get type and value in action node
				List<String> resultAttrs = getResultAttrs(targetElement, result);
				
				// handle result by type and value
				handleResult(resultAttrs, req, resp);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String html = "<html>\n" + "<head>\n" + "<meta charset=\"UTF-8\">\n" + "<title>Insert title here</title>\n"
					+ "</head>\n" + "<body>\n" + "	没有请求的资源\n" + "<br>\n" + "<a href=\"hello.html\"><font color=\"red\">返回主页</font></a>\n" + "</body>\n" + "</html>";
			resp.setContentType("text/html; charset=UTF-8");
			resp.getWriter().println(html);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		doPost(req, resp);
	
	}
	
	private Element getTargetElement(String action) {

		// first get action list
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(new File(this.getClass().getResource("/controller.xml").getFile()));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element rootElement = document.getRootElement();
		Element controllerElement = rootElement.element("controller");
		List<Element> actionList = controllerElement.elements("action");
		
		// then get target attributes
		Element targetElement = null;
		for (Element element : actionList) {
			if (action.equals(element.attributeValue("name"))) {
				targetElement = element;
				break;
			}
		}
		
		return targetElement;
	}
	
	private List<String> getActionAttrs(Element targetElement, String action) {
		
		List<String> actionAttrs = null;
		if (null != targetElement) {
			actionAttrs = new ArrayList<>();
			actionAttrs.add(targetElement.attributeValue("class"));
			actionAttrs.add(targetElement.attributeValue("method"));
		}
		
		return actionAttrs;
	}
	
	private List<String> getResultAttrs(Element targetElement, String result) {
		List<Element> resultList = targetElement.elements("result");
		Element resultElement = null;
		for (Element element : resultList) {
			if (result.equals(element.attributeValue("name"))) {
				resultElement = element;
				break;
			}
		}
		
		List<String> resultAttrs = null;
		if (null != resultElement) {
			resultAttrs = new ArrayList<>();
			resultAttrs.add(resultElement.attributeValue("type"));
			resultAttrs.add(resultElement.attributeValue("value"));
		}
		
		return resultAttrs;
	}
	
	private String getResult(List<String> attrs, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		Class<?> clazz = Class.forName(attrs.get(0));
		Constructor<?> clazzConstructor = clazz.getConstructor();
		Object clazzInstance = clazzConstructor.newInstance();
		Method clazzMethod = clazzInstance.getClass().getMethod(attrs.get(1), HttpServletRequest.class, HttpServletResponse.class);
		return (String) clazzMethod.invoke(clazzInstance, req, resp);
	}
	
	private void handleResult(List<String> resultAttrs, HttpServletRequest req, HttpServletResponse resp) {
		String type = resultAttrs.get(0);
		String value = resultAttrs.get(1);
		
		try {
			if ("forward".equals(type)) {
				req.getRequestDispatcher(value).forward(req, resp);
			} else if ("redirect".equals(type)) {
				resp.sendRedirect(value);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
