package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorStorage extends BaseStorage<Director> {

    void deleteById(long id);

}
