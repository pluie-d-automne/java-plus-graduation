package ru.practicum.collector.utils;

public class EnumMapper {
    public static <E extends Enum<E>, F extends Enum<F>> F map(E enum1, Class<F> enum2Class) {
        return Enum.valueOf(enum2Class, enum1.name());
    }
}
