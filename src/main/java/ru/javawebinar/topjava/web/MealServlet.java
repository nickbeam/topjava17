package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRestController controller;

    {
        ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        controller = appCtx.getBean(MealRestController.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (request.getParameter("action") != null) {
            if (request.getParameter("action").equals("user")) {
                String userId = request.getParameter("userId");
                log.info("Set current user id to: " + userId);
                SecurityUtil.setCurrentUser(Integer.valueOf(userId));
            }

            if (request.getParameter("action").equals("filter")) {
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                String startTime = request.getParameter("startTime");
                String endTime = request.getParameter("endTime");
                request.setAttribute("meals",
                        controller.getFiltered(
                                startDate.isEmpty() ? LocalDate.MIN : LocalDate.parse(startDate),
                                endDate.isEmpty() ? LocalDate.now() : LocalDate.parse(endDate),
                                startTime.isEmpty() ? LocalTime.MIN : LocalTime.parse(startTime),
                                endTime.isEmpty() ? LocalTime.MAX : LocalTime.parse(endTime)
                        )
                );
                log.info("getFiltered fromDate: " + startDate + " toDate: " + endDate + " fromTime: " + startTime + " toTime: " + endTime);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
            }

            if (request.getParameter("action").equals("meal")) {
                Integer id = request.getParameter("id").isEmpty() ? null : Integer.valueOf(request.getParameter("id"));
                Meal meal = new Meal(id,
                        LocalDateTime.parse(request.getParameter("dateTime")),
                        request.getParameter("description"),
                        Integer.parseInt(request.getParameter("calories")));

                if (meal.isNew()) {
                    log.info("Create {}", meal);
                    controller.create(meal);
                } else {
                    log.info("Update {}", meal);
                    controller.update(meal, Objects.requireNonNull(id));
                }
            }
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals", controller.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
