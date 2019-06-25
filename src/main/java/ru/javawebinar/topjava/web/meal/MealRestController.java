package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal) {
        int userId = authUserId();
        log.info("create meal {} of user {}", meal, userId);
        checkNew(meal);
        return service.create(userId, meal);
    }

    public void delete(int id) {
        int userId = authUserId();
        log.info("delete meal {} of user {}", id, userId);
        service.delete(userId, id);
    }

    public Meal get(int id) {
        int userId = authUserId();
        log.info("get meal {} of user {}", id, userId);
        return service.get(userId, id);
    }

    public void update(Meal meal, int id) {
        int userId = authUserId();
        log.info("update meal {} with id = {} of user {}", meal, id, userId);
        assureIdConsistent(meal, id);
        service.update(userId, meal);
    }

    public List<MealTo> getAll() {
        int userId = authUserId();
        log.info("getAll meals of user {}", userId);
        return service.getAll(userId, authUserCaloriesPerDay());
    }

    public List<MealTo> getFiltered(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        int userId = authUserId();
        log.info("getFiltered meals between date from {} to {} time from {} to {} of user {}",startDate, endDate, startTime, endTime, userId);

        return service.getFiltered(userId, startDate, endDate, startTime, endTime, authUserCaloriesPerDay());
    }
}