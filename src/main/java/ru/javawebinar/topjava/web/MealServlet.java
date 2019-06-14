package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.Config;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.storage.IStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MealServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MealServlet.class);
    private IStorage storage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        storage = Config.getInstance().getStorage();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(request.getParameter("id").trim());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String dt = request.getParameter("dateTime").trim();
        LocalDateTime dateTime = dt.length() < 5 ? LocalDateTime.now() : LocalDateTime.parse(request.getParameter("dateTime").replace("T", " "), dtf);
        String description = request.getParameter("description").trim();
        String calories = request.getParameter("calories").trim();
        Meal meal;
        if (id == 0 && description.equals("")) {
            LOG.debug("redirect to meals");
            response.sendRedirect("meals");
            return;
        } else if (id == 0 && !description.isEmpty()) {
            LOG.debug("add new meal");
            meal = new Meal(dateTime, description, Integer.parseInt(calories));
            storage.save(meal);
        } else {
            LOG.debug("update meal with id=" + id);
            meal = storage.get(id);
            meal.setDateTime(dateTime);
            meal.setDescription(description);
            meal.setCalories(Integer.parseInt(calories));
            storage.update(meal);
        }
        LOG.debug("redirect to meals");
        response.sendRedirect("meals");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        if (action == null) {
            LOG.debug("forward to meals");
            List<MealTo> mealList = MealsUtil.getFilteredWithExcess(storage.getAll(), LocalTime.of(0, 0), LocalTime.of(23, 59), 2000);
            request.setAttribute("meals", mealList);
            request.getRequestDispatcher("meals.jsp").forward(request, response);
            return;
        }
        Meal meal;
        switch (action) {
            case "add":
                LOG.debug("forward to add new meal page");
                meal = new Meal();
                break;
            case "delete":
                LOG.debug("delete meal with id=" + id);
                storage.delete(Integer.parseInt(id));
                response.sendRedirect("meals");
                return;
            case "edit":
                LOG.debug("forward to edit meal with id=" + id);
                meal = storage.get(Integer.parseInt(id));
                break;
            default:
                LOG.debug("Action " + action + " is illegal");
                throw new IllegalArgumentException("Action " + action + " is illegal");
        }
        request.setAttribute("meal", meal);
        request.getRequestDispatcher("edit.jsp").forward(request, response);
    }
}
