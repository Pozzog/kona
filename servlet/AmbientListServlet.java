package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import componenti.Ambiente;
import componenti.UserAccount;
import exceptions.NullException;
import exceptions.ZeroException;
import utils.*;
/**
 * 
 * @author gandalf
 *
 */
@WebServlet(urlPatterns = { "/ambientList" })
public class AmbientListServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	/**
	 * id dell'ambiente
	 */
	public static String id = null;
	/**
	 * indicatore per la scelta della vista collegata
	 * al pulsante premuto
	 */
	public static String way = null;
	/**
	 * id intero dell'ambiente
	 */
	public static int idInt = 0;
	/**
	 * indicatore per scegliere la vista 
	 * dipendentemente dall'utente loggato
	 */
	public static int status = 0;
	/**
	 * arraylist degli ambienti da visualizzare 
	 * nella pagina 
	 */
	static ArrayList<Ambiente> ambienti = null;

	public AmbientListServlet() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Connection conn = MyUtils.getStoredConnection(request);
		
		String errorString = null;
		ambienti = null;
		
		try {
			
			ambienti = DBUtils.queryAmbienti(conn, LoginServlet.name);	
			
			UserAccount user = DBUtils.findUser(conn, LoginServlet.name);
			
			status = user.getPrivilegi();

		} catch(SQLException e) {
			
			System.out.println("SQLException");
			errorString = e.getMessage();
			
		} catch (ZeroException e) {
			
			System.out.println("ZeroException");
			errorString = e.getMessage();
			
		} catch (NullException e) {
			
			System.out.println("NullException");
			errorString = e.getMessage();
		}
		
		
		//Store info in request attribute, before forward to views
		request.setAttribute("errorString", errorString);
		request.setAttribute("ambientList", ambienti);
		
		//Forward to /WEB-INF/views/ambientList.jsp
		if(status == 1) {
			RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/WEB-INF/views/ambientList.jsp");
		
			dispatcher.forward(request, response);
		}
		
		if(status == 0) {
			RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/WEB-INF/views/ambientListAdmin.jsp");
			
			dispatcher.forward(request, response);
		}
		
		if(status == 2) {
			RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/WEB-INF/views/ambientListSuperAd.jsp");
			
			dispatcher.forward(request, response);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		id = (request.getParameter("ambID"));
		way = (request.getParameter("way"));
		
		idInt = Integer.parseInt(id);
		int wayInt = Integer.parseInt(way);
		
		Ambiente amb = null;
		String errorString = null;
		boolean hasError = false;
		
		if(hasError) {
			
			amb = new Ambiente();
			try {
				amb.setId(idInt);
				
			} catch (ZeroException e) {

				System.out.println("ZeroException");
			}
			
			request.setAttribute("errorString", errorString);
			request.setAttribute("ambient", amb);
			
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/views/ambientList.jsp");
			
			dispatcher.forward(request, response);
		} 
		
		else {
			if(wayInt == 0)
				response.sendRedirect(request.getContextPath() + "/sensorList");
			
			if(wayInt == 1)
				response.sendRedirect(request.getContextPath() + "/summary");
			
			if(wayInt == 2)
				response.sendRedirect(request.getContextPath() + "/editAmbient");
			
			if(wayInt == 3)
				response.sendRedirect(request.getContextPath() + "/deleteAmbient");
			
			if(wayInt == 4)
				response.sendRedirect(request.getContextPath() + "/userList");
		}		
	}	
}
