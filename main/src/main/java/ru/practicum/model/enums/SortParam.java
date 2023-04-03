package ru.practicum.model.enums;


import java.util.Optional;

public enum SortParam {
    EVENT_DATE, VIEWS;

    public static Optional<SortParam> from(String stringSort) {
        for (SortParam state : values()) {
            if (state.name().equalsIgnoreCase(stringSort)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
    }
