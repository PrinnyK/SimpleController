package sc.ustc.bean;

import java.util.List;

import org.dom4j.Element;

public class ActionBean {
	private String actionName;
	private Element actionElement;

	private String actionClass;
	private String actionMethod;
	private List<InterceptorBean> interceptorList;
	
	private String result;
	private String resultType;
	private String resultValue;
	
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public Element getActionElement() {
		return actionElement;
	}
	public void setActionElement(Element actionElement) {
		this.actionElement = actionElement;
	}
	public String getActionClass() {
		return actionClass;
	}
	public void setActionClass(String actionClass) {
		this.actionClass = actionClass;
	}
	public String getActionMethod() {
		return actionMethod;
	}
	public void setActionMethod(String actionMethod) {
		this.actionMethod = actionMethod;
	}
	public List<InterceptorBean> getInterceptorList() {
		return interceptorList;
	}
	public void setInterceptorList(List<InterceptorBean> interceptorList) {
		this.interceptorList = interceptorList;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getResultValue() {
		return resultValue;
	}
	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

}