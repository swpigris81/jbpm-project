<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>登录</title>


<style type="text/css">
body {
	background: #026AA9;
	font-size: 12px;
	margin-left: auto;
	margin-right: a
}

table,table td {
	margin: 0;
	padding: 0;
	font-size: 12px;
}

.bj {
	background: url(<%=request.getContextPath()%>/image/loginbj.jpg )
		no-repeat;
	width: 969px;
	height: 439px;
}

* /
		.box {
	border: 1px #83BDD5 solid;
	background: #ffffff;
	width: 100px;
	font-size: 12px;
}
</style>

</head>
<body>
	<form id="loginForm" name="form1" method="post"
		action='testAction!login.action'>
		<input type="hidden" id="strPath" name="strPath" value="<%=path%>" />
		<table width="969" height="612" align="center" border="0"
			cellpadding="0" cellspacing="0">
			<tr>
				<td height="88">&nbsp;</td>
			</tr>
			<tr>
				<td height="432" class="bj">
					<table width="969" height="432" border="0" cellpadding="0"
						cellspacing="0">
						<tr height="180">
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td height="80">&nbsp;</td>
							<td>
								<!--    <table  width="210" height="85" border="1" align="left" cellpadding="0" cellspacing="3" style="height: 85px;">-->
								<table border="0">
									<tr>
										<td width="300"></td>
										<td>
											<table>
												<tr>
													<td width="60"><font color="#ffffff">机构:</font></td>
													<td colspan="2"><input type="text" value="9900"
														name="loginForm.branId" style="width: 120px"></td>
												</tr>
												<tr>
													<td><font color="#ffffff">操作员:</font></td>
													<td colspan="2"><input type="text" value="00001"
														name="loginForm.userId" style="width: 120px"></td>
												</tr>
												<tr>
													<td><font color="#ffffff">登录密码:</font></td>
													<td colspan="2"><input type="password" value="111111"
														name="loginForm.userPwd" style="width: 120px"></td>
												</tr>
											</table>
										</td>

										<td>
											<table>
												<tr>
													<td>&nbsp;&nbsp;</td>
													<td><input type="submit" value="登录"
														style="width: 80px"></td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
							<td height="85">&nbsp;</td>
						</tr>
						<tr>
							<td height="180">&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="85">&nbsp;</td>
			</tr>
		</table>
	</form>
</body>
</html>