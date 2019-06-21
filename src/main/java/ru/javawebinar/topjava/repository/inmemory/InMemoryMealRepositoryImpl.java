package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private Map<Integer, HashMap<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(authUserId(), meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (!repository.containsKey(userId)) {
            repository.put(userId, new HashMap<>());
        }
        HashMap<Integer, Meal> meals = repository.get(userId);
        if (meals.isEmpty()) {
            return null;
        }
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meals.put(meal.getId(), meal);
            repository.put(userId, meals);
            return meal;
        }
        // treat case: update, but absent in storage
        meals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        repository.put(userId, meals);
        return meal;
    }

    @Override
    public boolean delete(int userId, int id) {
        HashMap<Integer, Meal> meals = repository.get(userId);
        if (meals.isEmpty() || !meals.containsKey(id)) {
            return false;
        }
        meals.remove(id);
        return repository.put(userId, meals) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        HashMap<Integer, Meal> meals = repository.get(userId);
        if (meals.isEmpty()) {
            return null;
        }
        return meals.get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        HashMap<Integer, Meal> meals = repository.get(userId);
        if (meals.isEmpty()) {
            return null;
        }
        return meals.values();
    }
}

