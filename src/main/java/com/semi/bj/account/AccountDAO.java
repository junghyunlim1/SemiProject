package com.semi.bj.account;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.User;
import org.mindrot.jbcrypt.BCrypt;

import com.semi.db.DBManager;

public class AccountDAO {

	public static void login(HttpServletRequest request) {

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

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
				if (BCrypt.checkpw(pw, dbPw)) {
					System.out.println("로그인 성공");
					result = "로그인 성공!";
					Account userInfo = new Account();
					userInfo.setUser_id(rs.getString("user_id"));
					userInfo.setUser_name(rs.getString("user_name"));
					userInfo.setUser_pw(rs.getString("user_pw"));
					userInfo.setUser_create_at(rs.getString("user_create_at"));
					userInfo.setUser_gender(rs.getString("user_gender"));
					userInfo.setUser_question(rs.getString("user_question"));
					userInfo.setUser_answer(rs.getString("user_answer"));
// 					userInfo.setUser_points(rs.getString("user_points")); 포인트 삭제

					HttpSession hs = request.getSession();
					hs.setAttribute("account", userInfo);
					hs.setMaxInactiveInterval(60 * 300);
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

	public static void regAccount(HttpServletRequest request) throws UnsupportedEncodingException {

		request.setCharacterEncoding("UTF-8");
		Date date = new Date();

		Connection con = null;
		PreparedStatement pstmt = null;

		String sql = "insert into user_tbl values(?, ?, ?, sysdate, ?, ?, ?)";

		String email = request.getParameter("email");
		String nickname = request.getParameter("nickname");
		String pw = request.getParameter("password");
		String pw2 = BCrypt.hashpw(request.getParameter("password"), BCrypt.gensalt());
		String pwConfirm = request.getParameter("passwordConfirm");
		String gender = request.getParameter("gender");
		String question = request.getParameter("question");
		String answer = request.getParameter("answer");

		System.out.println(email);
		System.out.println(nickname);
		System.out.println(pw2);
		System.out.println(pwConfirm);
		System.out.println(gender);
		System.out.println(question);
		System.out.println(answer);

		try {

			con = DBManager.connect();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, email);
			pstmt.setString(2, nickname);
			pstmt.setString(3, pw2);
			pstmt.setString(4, gender);
			pstmt.setString(5, question);
			pstmt.setString(6, answer);

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

	public static void accountUpdate(HttpServletRequest request) throws UnsupportedEncodingException {
		request.setCharacterEncoding("UTF-8");

		Account account = (Account) request.getSession().getAttribute("account");
		Connection con = null;
		PreparedStatement pstmt = null;

		String sql = "update user_tbl set user_name = ?, user_pw = ?, user_gender = ? where user_id = ?";

		String nickname = request.getParameter("nickname");
		String pw = BCrypt.hashpw(request.getParameter("password"), BCrypt.gensalt());
		String pwConfirm = request.getParameter("passwordConfirm");
		String gender = request.getParameter("gender");
		String id = account.getUser_id();

		System.out.println(nickname);
		System.out.println(pw);
		System.out.println(pwConfirm);
		System.out.println(gender);
		System.out.println(id);

		try {
			con = DBManager.connect();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, nickname);
			pstmt.setString(2, pw);
			pstmt.setString(3, gender);
			pstmt.setString(4, id);

			if (pstmt.executeUpdate() == 1) {
				System.out.println("수정 성공");

				// 세션을 새로 업데이트
				account.setUser_name(nickname);
				account.setUser_pw(pw);
				account.setUser_pw(gender);
				HttpSession hs = request.getSession();
				hs.setAttribute("account", account);
				hs.setMaxInactiveInterval(60 * 300);
			}

		} catch (Exception e) {
			System.out.println("db연결 실패");
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, null);
		}
	}

	public static void accountDelete(HttpServletRequest request) {

		Connection con = null;
		PreparedStatement pstmt = null;
		Account account = (Account) request.getSession().getAttribute("account");

		String sql = "delete user_tbl where user_id = ?";

		try {
			con = DBManager.connect();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, account.getUser_id());

			if (pstmt.executeUpdate() == 1) {
				System.out.println("삭제 성공");
				logout(request);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, null);
		}
	}

	public static void duplicateCheck(HttpServletRequest request, HttpServletResponse response) {

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select user_id from user_tbl where user_id = ?";

		String email = request.getParameter("user_id");
		System.out.println(request.getParameter("user_id"));
		try {
			con = DBManager.connect();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();

			response.setContentType("text/html;charset=utf-8");
			PrintWriter writer = response.getWriter();

			if (rs.next()) {
				System.out.println("중복된 아이디 있음");
				response.setStatus(400);
				writer.write("false");
			} else {
				System.out.println("중복체크 통과 아이디 생성가능");
				response.setStatus(200);
				writer.write("true");
			}

		} catch (Exception e) {
			System.out.println("db연결 오류");
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, rs);
		}

	}

	public static void pwUpdate(HttpServletRequest request) {
		
		Account account = (Account) request.getSession().getAttribute("account");

		Connection con = null;
		PreparedStatement pstmt = null;

		String sql = "update user_tbl set user_pw = ?, where user_id = ?";

		String pw = request.getParameter("pw");
		String id = request.getParameter("id");

		try {
			con = DBManager.connect();

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, pw);
			pstmt.setString(2, id);

			if (pstmt.executeUpdate() == 1) {
				System.out.println("수정 성공");

				// 세션을 새로 업데이트
				account.setUser_pw(pw);
				HttpSession hs = request.getSession();
				hs.setAttribute("account", account);
				hs.setMaxInactiveInterval(60 * 300);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(con, pstmt, null);
		}

	}

}
