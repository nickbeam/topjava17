package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapMealStorage implements IStorage {
    private Map<Integer, Meal> storage = new ConcurrentHashMap<>();

    @Override
    public Meal get(int id) {
        return storage.get(id);
    }

    @Override
    public void save(Meal meal) {
        storage.put(meal.getId(), meal);
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }
}
