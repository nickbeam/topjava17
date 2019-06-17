package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface IStorage {

    Meal get(int id);

    void save(Meal meal);

    void delete(int id);

    List<Meal> getAll();
}
