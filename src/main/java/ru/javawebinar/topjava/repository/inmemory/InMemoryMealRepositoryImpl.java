package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    @Override
    public Collection<Meal> getBetween(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return getFiltered(userId, meal -> DateTimeUtil.isBetween(meal.getDateTime(), startDateTime, endDateTime));
    }

    private Collection<Meal> getFiltered(int userId, Predicate<Meal> filter) {
        ConcurrentHashMap<Integer, Meal> meals = repository.get(userId);
        return meals == null ? Collections.emptyList() : meals.values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

