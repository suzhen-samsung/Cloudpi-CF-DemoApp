<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Cache-Control" content="no-cache" />
<title>membase set K-V</title>
<script type="text/javascript" src="static/js/jquery.min.js"></script>
<script type="text/javascript">
	$(function() {
		$.ajaxSetup({
			cache : false
		});
		$('#form').submit(
				function() {
					$.post($('#form').attr("action"), $('#form').serialize(),
							function(response) {
								if (response) {
									confirm(response.id);
								}
							});
					$('#value').val("");
					return false;
				});
	});
</script>
</head>
<body>
	<div>
		<a href="get">Get value throw this page</a>
	</div>
	<h1>set value</h1>

	<form id="form" method="post" action="set">
		<table>
			<tr>
				<th>key:</th>
				<td><input id="key" name="key" value="" /></td>
			</tr>
			<tr>
				<th>value:</th>
				<td><input id="value" name="value" /><input id="send"
					type="submit" value="set" /></td>

			</tr>
			<tr>
				<th>hostname:</th>
				<td><select id="hostName" name="hostName">
						<c:forEach items="${hostname}" var="hostname">
							<option value="${hostname}">${hostname}</option>
						</c:forEach>
				</select></td>
			</tr>
		</table>
	</form>

	<hr />

</body>
</html>
