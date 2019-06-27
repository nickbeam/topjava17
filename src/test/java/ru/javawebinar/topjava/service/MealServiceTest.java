package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL1.getId(), USER_ID);
        assertMatch(meal, MEAL1);
    }

    @Test(expected = NotFoundException.class)
    public void getWrongUserMeal() throws Exception{
        service.get(MEAL1.getId(), ADMIN_ID);
    }

    @Test
    public void delete() {
        service.delete(MEAL_ADMIN2.getId(), ADMIN_ID);
        assertMatch(service.getAll(ADMIN_ID), MEAL_ADMIN1);
    }

    @Test(expected = NotFoundException.class)
    public void deleteWrongUserMeal() throws Exception{
        service.delete(MEAL_ADMIN2.getId(), USER_ID);
    }

    @Test
    public void getBetweenDates() {
        List<Meal> meals = service.getBetweenDates(LocalDate.of(2015, 5, 30), LocalDate.of(2015, 5, 30), USER_ID);
        assertMatch(meals, MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void getBetweenDateTimes() {
        List<Meal> meals = service.getBetweenDateTimes(LocalDateTime.of(2015, 5, 30, 10, 0), LocalDateTime.of(2015, 5, 30, 13, 0), USER_ID);
        assertMatch(meals, MEAL2, MEAL1);
    }

    @Test
    public void getAll() {
        List<Meal> meals = service.getAll(ADMIN_ID);
        assertMatch(meals, MEAL_ADMIN2, MEAL_ADMIN1);
    }

    @Test
    public void update() {
        Meal updated = new Meal(MEAL1);
        updated.setDescription("New description");
        updated.setCalories(777);
        service.update(updated, USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), updated);
    }

    @Test(expected = NotFoundException.class)
    public void updateWrongUserMeal() throws Exception {
        Meal updated = new Meal(MEAL1);
        updated.setDescription("New description");
        updated.setCalories(777);
        service.update(updated, ADMIN_ID);
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(LocalDateTime.of(2019, 6, 27, 10, 0), "New meal", 555);
        Meal created = service.create(newMeal, ADMIN_ID);
        newMeal.setId(created.getId());
        assertMatch(service.getAll(ADMIN_ID), newMeal, MEAL_ADMIN2, MEAL_ADMIN1);
    }

    @Test(expected = DataAccessException.class)
    public void createDuplicateDateUserMeal() throws Exception{
        Meal newMeal = new Meal(MEAL_ADMIN1.getDateTime(), "Duplicate meal", 999);
        service.create(newMeal, ADMIN_ID);
    }
}