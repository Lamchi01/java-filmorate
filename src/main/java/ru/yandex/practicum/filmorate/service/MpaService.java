package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {
    private final BaseStorage<Mpa> mpaStorage;

    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(Long id) {
        return mpaStorage.findById(id);
    }

    public Mpa create(Mpa mpa) {
        return mpaStorage.create(mpa);
    }

    public Mpa update(Mpa mpa) {
        Mpa savedMpa = mpaStorage.findById(mpa.getId());
        if (mpa.getName() != null) savedMpa.setName(mpa.getName());
        mpaStorage.update(mpa);
        return savedMpa;
    }

    public void deleteAllMpa() {
        mpaStorage.deleteAll();
    }
}
