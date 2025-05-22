package org.example.models;

import java.util.Objects;

/**
 * Класс, представляющий координаты.
 * Реализует безопасную установку полей с проверкой входных данных.
 */
public class Coordinates {
    private Double x; // Максимальное значение: 709, не может быть null
    private long y;   // Должно быть > -4

    /**
     * Устанавливает координату X с проверкой ограничений.
     * @param x значение координаты X
     * @return true - если значение установлено, false - если значение невалидное
     */
    public boolean setX(Double x) {
        if (x == null || x > 709) {
            return false;
        }
        this.x = x;
        return true;
    }

    /**
     * Устанавливает координату Y с проверкой ограничений.
     * @param y значение координаты Y
     * @return true - если значение установлено, false - если значение невалидное
     */
    public boolean setY(long y) {
        if (y <= -4) {
            return false;
        }
        this.y = y;
        return true;
    }

    public Double getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return y == that.y && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Coordinates[x=%.2f, y=%d]", x, y);
    }
}