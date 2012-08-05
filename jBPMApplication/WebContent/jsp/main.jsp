<%@page import="com.huateng.jbpm.test.client.db.model.LoginForm"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    LoginForm loginForm = (LoginForm)request.getSession().getAttribute("userInfo");
%>
<html>
	<form action="testAction!newTask.action">
		<input type="text" name="userId" id="userId" value="<%=loginForm.getUserId() %>"/>
		<input type="submit" value="提交"/>
	</form>
	<form action="testAction!getTasks.action">
		<input type="text" name="taskId" id="taskId" value=""/>
		<input type="submit" value="提交"/>
	</form> 
</html>