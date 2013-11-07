<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Cache-Control" content="no-cache" />
<title>membase:get Key Value</title>
<script language="javascript" type="text/javascript" src="jquery.min.js"></script>
<script language="javascript" type="text/javascript"></script>
</head>
<body>
	<h1>get Key-Value</h1>
	<form id="form" method="post" action="doGet">
		<table>
			<tr>
				<th>key:</th>
				<td><input id="key" name="key" value="" /></td>
			</tr>
			<tr>
				<th>choose host:</th>
				<td><select id="hostName" name="hostName">
						<c:forEach items="${hostname}" var="hostname">
							<option value="${hostname}">${hostname}</option>
						</c:forEach>
				</select> <input id="send" type="submit" value="get" /></td>
			</tr>
		</table>
	</form>
	<hr />
</body>
</html>
