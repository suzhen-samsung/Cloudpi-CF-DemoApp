<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Use Cloudpi Dedicated Services MongoDB DEMO</title>
<link rel="stylesheet" href="static/css/main.css" type="text/css"></link>
<link rel="stylesheet" href="static/css/colors.css" type="text/css"></link>
<link rel="stylesheet" href="static/css/local.css" type="text/css"></link>
</head>
<body>
	<div id="page">
		<div id="header">
			<div id="name-and-company">
				<div id='site-name'>
					<a href="" title="Insert data into MongoDB" rel="result">Insert
						data into MongoDB </a>
				</div>
				<div id='company-name'>
					<a href="http://www.springsource.com" title="SpringSource">SpringSource
						Home</a>
				</div>
			</div>
			<!-- /name-and-company -->
		</div>
		<!-- /header -->
		<div id="container">
			<div id="content" class="no-side-nav">

				<p>
					Every time you reload the page, another Person object is created
					and stored in MongoDB. The database is then queried and each person
					added to the list you see below. To reset the list, click <a
						href="deleteAll">Delete All</a>
				</p>
				<h2>The following people have been stored in the database:</h2>
				<ul>
					<c:forEach items="${people}" var="people">
						<li><p>${people}</p></li>
					</c:forEach>
				</ul>

				<h2>The following services have been bound to this application:</h2>
				<ul>
					<c:forEach items="${services}" var="service">
						<li><p>${service}</p></li>
					</c:forEach>
				</ul>
				<h2>The following services properties available to the
					application are available:</h2>
				<ul>
					<c:forEach items="${serviceProperties}" var="serviceProperties">
						<li><p>${serviceProperties}</p></li>
					</c:forEach>
				</ul>


			</div>
		</div>
	</div>
</body>
</html>
