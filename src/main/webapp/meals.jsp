<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>

<a href="meals?&action=add"><img src="img/add.png"> Add meal</a>
<table border="1" cellpadding="8" cellspacing="0">
    <thead>
    <tr>
        <th>Date&Time</th>
        <th>Description</th>
        <th>Calories</th>
        <th colspan="2">Action</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="meal" items="${meals}">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr id=${meal.excess?"red":"green"}>
            <td>${meal.dateTime.toString().replace('T', ' ')}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="meals?id=${meal.id}&action=edit"><img src="img/pencil.png" alt="Delete"></a></td>
            <td><a href="meals?id=${meal.id}&action=delete"><img src="img/delete.png" alt="Delete"></a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>