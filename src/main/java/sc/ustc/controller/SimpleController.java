package sc.ustc.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleController extends HttpServlet{
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String html = 	"<html>\n" + 
							"<head>\n" + 
								"<title>SimplerController</title>\n" + 
							"</head>\n" + 
							"<body>\n" + 
								"欢迎使用SimplerController!\n" + 
							"</body>\n" + 
						"</html>";
		resp.setContentType("text/html; charset=UTF-8");
		resp.getWriter().println(html);
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		doPost(req, resp);
	
	}
}
