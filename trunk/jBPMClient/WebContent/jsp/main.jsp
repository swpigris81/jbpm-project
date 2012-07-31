<%@page import="com.huateng.db.model.LoginForm"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    LoginForm loginForm = (LoginForm)request.getSession().getAttribute("userInfo");
%>
<html>
	<form action="testAction!getTasks.action">
		<input type="text" name="userId" id="userId" value="<%=loginForm.getUserId() %>"/>
		<input type="submit" value="提交"/>
	</form>
</html>