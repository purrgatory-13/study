package org.example.models;

import java.util.Objects;

/**
 * Класс, представляющий место проведения мероприятия.
 * Реализует автоматическую генерацию ID и валидацию полей.
 */
public class Venue {
    private Long id; // Автоматически генерируемый уникальный ID (> 0)
    private String name; // Не может быть null или пустым
    private int capacity; // Должно быть > 0
    private VenueType type; // Может быть null

    private static long lastGeneratedId = 0; // Счетчик для генерации ID

    /**
     * Конструктор с автоматической генерацией ID.
     */
    public Venue() {
        this.id = generateId();
    }

    /**
     * Генерирует уникальный ID.
     * @return сгенерированный ID (> 0)
     */
    private static synchronized long generateId() {
        return ++lastGeneratedId;
    }

    /**
     * Устанавливает название места проведения.
     * @param name название (не может быть null или пустым)
     * @return true - если значение установлено, false - если невалидное
     */
    public boolean setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        this.name = name;
        return true;
    }

    /**
     * Устанавливает вместимость места.
     * @param capacity вместимость (> 0)
     * @return true - если значение установлено, false - если невалидное
     */
    public boolean setCapacity(int capacity) {
        if (capacity <= 0) {
            return false;
        }
        this.capacity = capacity;
        return true;
    }

    /**
     * Устанавливает тип места.
     * @param type тип места (может быть null)
     */
    public void setType(VenueType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public VenueType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venue venue = (Venue) o;
        return Objects.equals(id, venue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format(
                "Venue[id=%d, name='%s', capacity=%d, type=%s]",
                id, name, capacity, type != null ? type : "не указан"
        );
    }

    /**
     * Устанавливает тип места из строки.
     * @param typeStr строковое представление типа
     * @return true - если тип установлен, false - если строка не соответствует ни одному типу
     */
    public boolean setTypeFromString(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            this.type = null;
            return true;
        }
        try {
            this.type = VenueType.valueOf(typeStr.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}