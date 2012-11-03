<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="/jsp/common/config.jsp" %>
<script type="text/javascript" src="<%=path%>/js/util/dataStore.js"></script>
<script type="text/javascript" src="<%=path%>/js/ext-2.2.1/source/plugins/autocolumn/TaskQueue.js"></script>
<script type="text/javascript" src="<%=path%>/js/ext-2.2.1/source/plugins/autocolumn/ColumnWidthCalculator.js"></script>

<!-- Ext上传组件必须css以及js插件 begin -->
<link href="<%=path %>/css/file-upload.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=path %>/js/ext-2.2.1/source/ux/FileUploadField.js"></script>
<!-- Ext上传组件必须css以及js插件 end -->

<script type="text/javascript" src="<%=path%>/js/webservice/loan/loan.js"></script>
<title>请款</title>
</head>
<body>
	<div style="width: 100%;height: 100%" id="loan_div"></div>
</body>
</html>