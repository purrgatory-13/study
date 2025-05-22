package org.example.models;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Класс, представляющий билет.
 */
public class Ticket implements Comparable<Ticket> {
    private int id; // > 0, уникальное, генерируется автоматически
    private String name; // не null, не пустое
    private Coordinates coordinates; // не null
    private ZonedDateTime creationDate; // не null, генерируется автоматически
    private int price; // > 0
    private String comment; // не null, не пустое
    private Boolean refundable; // может быть null
    private TicketType type; // не null
    private Venue venue; // не null

    private static int lastGeneratedId = 0;

    public Ticket() {
        this.id = generateId();
        this.creationDate = ZonedDateTime.now();
    }

    private static synchronized int generateId() {
        return ++lastGeneratedId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public int getPrice() { return price; }
    public String getComment() { return comment; }
    public Boolean getRefundable() { return refundable; }
    public TicketType getType() { return type; }
    public Venue getVenue() { return venue; }

    // Безопасные сеттеры с валидацией
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть больше 0");
        }
        this.id = id;
    }

    public boolean setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        this.name = name;
        return true;
    }

    public boolean setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            return false;
        }
        this.coordinates = coordinates;
        return true;
    }

    public boolean setPrice(int price) {
        if (price <= 0) {
            return false;
        }
        this.price = price;
        return true;
    }

    public boolean setComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            return false;
        }
        this.comment = comment;
        return true;
    }

    public void setRefundable(Boolean refundable) {
        this.refundable = refundable;
    }

    public boolean setType(TicketType type) {
        if (type == null) {
            return false;
        }
        this.type = type;
        return true;
    }

    public boolean setVenue(Venue venue) {
        if (venue == null) {
            return false;
        }
        this.venue = venue;
        return true;
    }

    @Override
    public int compareTo(Ticket other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id == ticket.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format(
                "Ticket[id=%d, name='%s', coordinates=%s, creationDate=%s, price=%d, comment='%s', refundable=%s, type=%s, venue=%s]",
                id, name, coordinates, creationDate, price, comment, refundable, type, venue
        );
    }
}