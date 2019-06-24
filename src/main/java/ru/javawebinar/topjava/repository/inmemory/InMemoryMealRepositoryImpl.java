package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private Map<Integer, ConcurrentHashMap<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS_ADMIN.forEach(meal -> save(1, meal));
        MealsUtil.MEALS_USER.forEach(meal -> save(2, meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        repository.putIfAbsent(userId, new ConcurrentHashMap<>());
        ConcurrentHashMap<Integer, Meal> meals = repository.get(userId);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meals.put(meal.getId(), meal);
            repository.put(userId, meals);
            return meal;
        }
        meals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        repository.put(userId, meals);
        return meal;
    }

    @Override
    public boolean delete(int userId, int id) {
        ConcurrentHashMap<Integer, Meal> meals = repository.get(userId);
        return meals.remove(id) != null && repository.put(userId, meals) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        ConcurrentHashMap<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        ConcurrentHashMap<Integer, Meal> meals = repository.get(userId);
        return meals == null ? Collections.emptyList() : meals.values();
    }
}

