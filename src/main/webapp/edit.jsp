<%@ page import="ru.javawebinar.topjava.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <title>Add/Edit Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Add/Edit Meal</h2>

<form method="post" action="meals" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="id" value="${meal.id}">
    <dl>
        <dt>Date&Time</dt>
        <dd><input type="datetime-local" name="dateTime" value="${meal.dateTime}"></dd>
    </dl>
    <dl>
        <dt>Description</dt>
        <dd><input type="text" name="description" value="${meal.description}"></dd>
    </dl>
    <dl>
        <dt>Calories</dt>
        <dd><input type="number" name="calories" value="${meal.calories}"></dd>
    </dl>
    <button type="submit">Save</button>
    <button onclick="window.history.back()" type="button">Cancel</button>
</form>

</body>
</html>