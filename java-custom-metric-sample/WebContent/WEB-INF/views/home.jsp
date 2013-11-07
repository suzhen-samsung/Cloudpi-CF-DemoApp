<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Monitoring sample</title>
<script type="text/javascript" src="static/js/jquery.min.js"></script>
<script type="text/javascript">
</script>
</head>
<body>
	<form id="valueForm" method="post" action="send">
		<table>
			<tr>
				metric name
				<textarea id="name" name="name" rows="1" cols="8"></textarea>
				value
				<textarea id="value" name="value" rows="1" cols="8"></textarea>
				<input id="send" type="submit" value="send" />
			</tr>
		</table>
		<p>API usage</p>
		<p>/sample</p>
		<p>/count?name=users&value=50000</p>
	</form>
</body>
</html>
