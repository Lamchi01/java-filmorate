package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.validator.Marker;

@Valid
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Mpa {
    @NotNull
    private Integer id;

    //@Valid
    //@NotNull(groups = Marker.OnCreate.class, message = "name Mpa - null")
    private String name;
}
