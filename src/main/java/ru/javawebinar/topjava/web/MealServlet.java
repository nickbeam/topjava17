package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;

public class MealServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MealServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("redirect to meals");
        request.setAttribute("meals", MealsUtil.getFilteredWithExcess(Meal.meals, LocalTime.of(0, 0), LocalTime.of(23, 59), 2000));
        request.getRequestDispatcher("meals.jsp").forward(request, response);
//        request.getRequestDispatcher("/meals.jsp").forward(request, response);
        //response.sendRedirect("meals.jsp");
    }
}
