package com.semi.sh;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.semi.bj.account.AccountDAO;

@WebServlet("/QnAUpdateC")
public class QnAUpdateC extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AccountDAO.getAccountdao().loginCheck(request);
		QnADAO.getQnADAO().getQnA(request);
		QnADAO.getQnADAO().makebody2(request);
		request.setAttribute("contentPage", "jsp/sh/QnA_update.jsp");
		request.getRequestDispatcher("index.jsp").forward(request, response);
			
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AccountDAO.getAccountdao().loginCheck(request);
		QnADAO.getQnADAO().updateQnA(request);
		QnADAO.getQnADAO().getQnA(request);
		QnADAO.getQnADAO().makebody(request);
		request.setAttribute("contentPage", "jsp/sh/QnA_detail.jsp");
		request.getRequestDispatcher("index.jsp").forward(request, response);

	}

}
