<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>rabbitMQ message</title>
</head>
<body>
	<h1>get message</h1>
	<form id="form" method="post" action="chatlog">
		<table>

			<tr>
				<th>choose host:</th>
				<td><select id="hostname" name="hostname">
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
