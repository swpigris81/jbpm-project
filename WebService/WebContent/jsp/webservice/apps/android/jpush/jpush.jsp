<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="/jsp/common/config.jsp" %>
<script type="text/javascript" src="<%=path%>/js/util/dataStore.js"></script>

<script type="text/javascript" src="<%=path%>/js/webservice/apps/android/jpush/jpush.js"></script>
<title>Android消息推送</title>
</head>
<body>
	<div style="width: 100%;height: 100%" id="jpush_div"></div>
</body>
</html>