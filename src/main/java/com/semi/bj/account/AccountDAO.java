package com.semi.bj.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.semi.db.DBManager;

public class AccountDAO {

	public static void login(HttpServletRequest request) {

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		Date date = new Date();

		String email = request.getParameter("email");
		String pw = request.getParameter("password");
		String result = "";
		
		System.out.println(email);
		System.out.println(pw);

		String sql = "select * from user_tbl where user_id = ?";
		try {
			con = DBManager.connect();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				String dbPw = rs.getString("user_pw");
				if (pw.equals(dbPw)) {
					System.out.println("로그인 성공");
					result = "로그인 성공!";
					Account userInfo = new Account();
					userInfo.setUser_id("user_id");
					userInfo.setUser_name("user_name");
					userInfo.setUser_pw("user_pw");
					userInfo.setUser_create_at("user_create_at");
					userInfo.setUser_gender("user_gender");
					userInfo.setUser_points("user_points");

					HttpSession hs = request.getSession();
					hs.setAttribute("account", userInfo);
					hs.setMaxInactiveInterval(60);
				} else {
					System.out.println("비밀번호 오류");
					result = "비밀번호가 맞지 않습니다";
				}
				
			} else {
				System.out.println("아이디 오류");
				result = "존재하지 않는 회원입니다";
			}
			request.setAttribute("result", result);
			
		} catch (Exception e) {
			System.out.println("db접속 오류");
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, rs);
		}

	}

	public static void logout(HttpServletRequest request) {
		
		HttpSession hs = request.getSession();
		
		hs.setAttribute("account", null);
		loginCheck(request);
	}
	
	public static boolean loginCheck(HttpServletRequest request) {
		Account account = (Account) request.getSession().getAttribute("account");
		if (account == null) {
			request.setAttribute("LoginPage", "jsp/bj/login/navbarLogin.jsp");
			return false;
		} else {
			request.setAttribute("LoginPage", "jsp/bj/login/navbarLoginOK.jsp");
			return true;
		}

	}

	public static void regAccount(HttpServletRequest request) {

		Date date = new Date();

		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "insert into user_tbl values(?, ?, ?, sysdate, ?, '0')";

		String email = request.getParameter("email");
		String nickname = request.getParameter("nickname");
		String pw = request.getParameter("password");
		String pwConfirm = request.getParameter("passwordConfirm");
		String gender = request.getParameter("gender");

		System.out.println(email);
		System.out.println(nickname);
		System.out.println(pw);
		System.out.println(pwConfirm);
		System.out.println(gender);
		
		try {

			con = DBManager.connect();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, email);
			pstmt.setString(2, nickname);
			pstmt.setString(3, pw);
			pstmt.setString(4, gender);
			
			if (pstmt.executeUpdate() == 1) {
				System.out.println("등록 성공");
				request.setAttribute("result", "회원가입 완료");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			DBManager.close(con, pstmt, null);
		}

	}


}
