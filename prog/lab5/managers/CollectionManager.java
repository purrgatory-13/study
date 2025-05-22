package org.example.managers;

import org.example.models.Ticket;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Менеджер для работы с коллекцией билетов
 */
public class CollectionManager {
    private final LinkedList<Ticket> collection = new LinkedList<>();
    private final ZonedDateTime initDate = ZonedDateTime.now();

    /**
     * Удаляет билет по ID
     */
    public boolean removeById(long id) {
        return collection.removeIf(t -> t.getId() == id);
    }

    /**
     * Очищает коллекцию
     */
    public void clear() {
        collection.clear();
    }

    /**
     * Возвращает информацию о коллекции
     */
    public String getInfo() {
        return String.format(
                "Тип: %s\nДата инициализации: %s\nКоличество элементов: %d",
                collection.getClass().getSimpleName(),
                initDate,
                collection.size()
        );
    }

    /**
     * Возвращает немодифицируемое представление коллекции
     */
    public List<Ticket> getCollection() {
        return Collections.unmodifiableList(collection);
    }

    public String getCollectionType() {
        return collection.getClass().getSimpleName();
    }

    public ZonedDateTime getInitDate() {
        return initDate;
    }

    public int getCollectionSize() {
        return collection.size();
    }

    /**
     * Добавляет билет в коллекцию
     */
    public boolean add(Ticket ticket) {
        return collection.add(ticket);
    }

    /**
     * Возвращает элемент по ID
     */
    public Optional<Ticket> getById(long id) {
        return collection.stream()
                .filter(ticket -> ticket.getId() == id)
                .findFirst();
    }

    /**
     * Обновляет билет по ID
     */
    public boolean updateById(long id, Ticket newTicket) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId() == id) {
                collection.set(i, newTicket);
                return true;
            }
        }
        return false;
    }
}
