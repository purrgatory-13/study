package net.proselyte.web3.db.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "points")
public class PointModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 52, scale = 20)
    private BigDecimal x;

    @Column(nullable = false, precision = 52, scale = 20)
    private BigDecimal y;

    @Column(nullable = false, precision = 52, scale = 20)
    private BigDecimal r;

    @Column(nullable = false)
    private boolean hit;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "execution_time")
    private Long executionTime; // Время выполнения в миллисекундах

    public PointModel() {
    }

    public PointModel(BigDecimal x, BigDecimal y, BigDecimal r, boolean hit) {
        this.x = normalizeX(x);
        this.y = normalize(y);
        this.r = normalize(r);
        this.hit = hit;
        this.createdAt = LocalDateTime.now();
    }

    public PointModel(BigDecimal x, BigDecimal y, BigDecimal r, boolean hit, Long executionTime) {
        this.x = normalizeX(x);
        this.y = normalize(y);
        this.r = normalize(r);
        this.hit = hit;
        this.createdAt = LocalDateTime.now();
        this.executionTime = executionTime;
    }

    // Для X координаты: не округляем, сохраняем точное значение
    private BigDecimal normalizeX(BigDecimal value) {
        if (value == null) {
            return null;
        }
        // Для нуля возвращаем BigDecimal.ZERO
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // Возвращаем значение как есть, без stripTrailingZeros
        return value;
    }

    private BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            return null;
        }
        // Для нуля возвращаем BigDecimal.ZERO, иначе применяем stripTrailingZeros
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return value.stripTrailingZeros();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getX() {
        return x;
    }

    public void setX(BigDecimal x) {
        this.x = normalizeX(x);
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = normalize(y);
    }

    public BigDecimal getR() {
        return r;
    }

    public void setR(BigDecimal r) {
        this.r = normalize(r);
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    // Методы для форматированного отображения
    public String getXFormatted() {
        if (x == null) return "";
        return formatBigDecimal(x);
    }

    public String getYFormatted() {
        if (y == null) return "";
        return formatBigDecimal(y);
    }

    public String getRFormatted() {
        if (r == null) return "";
        return formatBigDecimal(r);
    }

    private String formatBigDecimal(BigDecimal value) {
        if (value == null) return "";
        // Если значение равно нулю, возвращаем "0"
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        // Убираем лишние нули и возвращаем строку
        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.toPlainString();
    }
}
