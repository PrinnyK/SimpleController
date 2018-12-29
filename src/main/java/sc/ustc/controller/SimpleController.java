package sc.ustc.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import sc.ustc.bean.ActionBean;
import sc.ustc.bean.InterceptorBean;
import sc.ustc.util.XMLUtil;

public class SimpleController extends HttpServlet{
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// create actionBean instance to store args
		ActionBean actionBean = new ActionBean();
		
		// initial actionBean
		String actionName = getActionName(req);
		actionBean.setActionName(actionName);
		String controllerPath = this.getClass().getResource("/controller.xml").getPath();
		initialAcitonBean(actionBean, controllerPath);
		
		try {
			doAction(actionBean, req, resp);
			
			// handle result by type and value
			handleResult(actionBean, req, resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		doPost(req, resp);
	
	}
	
	private void initialAcitonBean(ActionBean actionBean, String configPath) {
		// get rootElement
		Document document = XMLUtil.getXMLDocument(configPath);
		Element rootElement = document.getRootElement();
		
		// get action node
		Element actionElement = getActionElement(rootElement, actionBean);
		actionBean.setActionElement(actionElement);
		if (null != actionElement) {
			
			// write actionClass and actionMethod into actionBean
			String actionClass = actionElement.attributeValue("class");
			String actionMethod = actionElement.attributeValue("method");
			actionBean.setActionClass(actionClass);
			actionBean.setActionMethod(actionMethod);
			
			// write interceptorList into actionBean if any interceptor exists
			List<Element> interceptorElementList = actionElement.elements("interceptor-ref");
			if (null != interceptorElementList) {
				List<Element> interceptorDetailList = rootElement.elements("interceptor");
				List<InterceptorBean> interceptorBeanList = new ArrayList<>();
				for (Element element : interceptorElementList) {
					String interceptorName = element.attributeValue("name");

					Element interceptorElement = XMLUtil.getTargetElementFromList(interceptorDetailList,
							interceptorName);
					String interceptorClass = interceptorElement.attributeValue("class");
					String interceptorPredo = interceptorElement.attributeValue("predo");
					String interceptorAfterdo = interceptorElement.attributeValue("afterdo ");

					InterceptorBean interceptorBean = new InterceptorBean();
					interceptorBean.setInterceptorClass(interceptorClass);
					interceptorBean.setInterceptorPredo(interceptorPredo);
					interceptorBean.setInterceptorAfterdo(interceptorAfterdo);
					
					interceptorBeanList.add(interceptorBean);
				}
				
				actionBean.setInterceptorList(interceptorBeanList);
			}
		}
	}
	
	private String getActionName(HttpServletRequest req) {
		String[] reqURI = req.getRequestURI().split("/");
		return reqURI[2].substring(0, reqURI[2].lastIndexOf("."));
	}
	
	private Element getActionElement(Element rootElement, ActionBean actionBean) {
		String actionName = actionBean.getActionName();
		
		// first get action list
		Element controllerElement = rootElement.element("controller");
		List<Element> actionList = controllerElement.elements("action");
		
		// then get target attributes
		Element targetElement = XMLUtil.getTargetElementFromList(actionList, actionName);
		
		return targetElement;
	}
	
	private void doAction(ActionBean actionBean, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String actionClass = actionBean.getActionClass();
		if (null != actionClass) {
			Class<?> clazz = Class.forName(actionClass);
			Object clazzInstance = clazz.getConstructor().newInstance();
			Object proxy = clazzInstance;
			// to-do: multiple proxy
			List<InterceptorBean> interceptorBeanList = actionBean.getInterceptorList();
			if (null != interceptorBeanList) {
				InterceptorBean interceptorBean = interceptorBeanList.get(0);
				Class<?> interceptorClazz = Class.forName(interceptorBean.getInterceptorClass());
				Object interceptorInstance = interceptorClazz.getConstructor().newInstance();
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(clazzInstance.getClass());
				enhancer.setCallback((Callback) interceptorInstance);
				proxy = enhancer.create();
			}
			Method clazzMethod = clazzInstance.getClass().getMethod(actionBean.getActionMethod(), ActionBean.class,
					HttpServletRequest.class, HttpServletResponse.class);
			String result = (String) clazzMethod.invoke(proxy, actionBean, req, resp);

			actionBean.setResult(result);
		}
	}
	
	private void handleResult(ActionBean actionBean, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String result = actionBean.getResult();
		if (null != result) {
			Element actionElement = actionBean.getActionElement();
			List<Element> resultList = actionElement.elements("result");
			Element resultElement = XMLUtil.getTargetElementFromList(resultList, result);
			String type = resultElement.attributeValue("type");
			String value = resultElement.attributeValue("value");

			actionBean.setResultType(type);
			actionBean.setResultValue(value);
			
			if (value.endsWith("_view.xml")) {
				resp.getWriter().write(XMLUtil.ConvertXml2Html("/success_view.xsl", "/success_view.xml").toString());
			} else if ("forward".equals(type)) {
				req.getRequestDispatcher(value).forward(req, resp);
			} else if ("redirect".equals(type)) {
				resp.sendRedirect(value);
			}
		} else {
			String html = "<html>\n" + "<head>\n" + "<meta charset=\"UTF-8\">\n" + "<title>Insert title here</title>\n"
					+ "</head>\n" + "<body>\n" + "	没有请求的资源\n" + "<br>\n"
					+ "<a href=\"hello.html\"><font color=\"red\">返回主页</font></a>\n" + "</body>\n" + "</html>";
			resp.setContentType("text/html; charset=UTF-8");
			resp.getWriter().println(html);
		}
	}
}
