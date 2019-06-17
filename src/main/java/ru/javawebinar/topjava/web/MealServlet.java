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
import java.time.LocalDateTime;
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
        String id = request.getParameter("id");
        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description").trim(),
                Integer.valueOf(request.getParameter("calories")));
        LOG.debug("save meal");
        storage.save(meal);
        LOG.debug("redirect to meals");
        response.sendRedirect("meals");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        if (action == null) {
            LOG.debug("forward to meals");
            List<MealTo> mealList = MealsUtil.getWithExcess(storage.getAll(), MealsUtil.DEFAULT_CALORIES_PER_DAY);
            request.setAttribute("meals", mealList);
            request.getRequestDispatcher("meals.jsp").forward(request, response);
            return;
        }
        Meal meal;
        switch (action) {
            case "add":
                LOG.debug("forward to add new meal page");
                meal = new Meal(LocalDateTime.now(), "New meal", 1000);
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
