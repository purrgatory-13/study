package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.OutputManager;
import org.example.models.Ticket;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Команда для отображения всех элементов коллекции
 */
public class ShowCommand implements Command {
    private final CollectionManager collectionManager;
    private final OutputManager outputManager;

    public ShowCommand(CollectionManager collectionManager, OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.outputManager = outputManager;
    }

    @Override
    public void execute(String args) {
        if (!args.isEmpty()) {
            outputManager.printError("Команда 'show' не принимает аргументов");
            return;
        }

        List<Ticket> tickets = collectionManager.getCollection();
        if (tickets.isEmpty()) {
            outputManager.println("Коллекция пуста.");
            return;
        }

        outputManager.println("Элементы коллекции (" + tickets.size() + "):");
        outputManager.println("----------------------------------------");

        for (Ticket ticket : tickets) {
            outputManager.println(ticketToString(ticket));
            outputManager.println("----------------------------------------");
        }
    }

    private String ticketToString(Ticket ticket) {
        return String.format(
                "ID: %d\n" +
                        "Название: %s\n" +
                        "Координаты: (x=%.2f, y=%d)\n" +
                        "Дата создания: %s\n" +
                        "Цена: %d\n" +
                        "Комментарий: %s\n" +
                        "Возвратный: %s\n" +
                        "Тип: %s\n" +
                        "Место проведения:\n" +
                        "  ID: %d\n" +
                        "  Название: %s\n" +
                        "  Вместимость: %d\n" +
                        "  Тип: %s",
                ticket.getId(),
                ticket.getName(),
                ticket.getCoordinates().getX(),
                ticket.getCoordinates().getY(),
                ticket.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                ticket.getPrice(),
                ticket.getComment(),
                ticket.getRefundable() != null ? ticket.getRefundable() : "не указано",
                ticket.getType(),
                ticket.getVenue().getId(),
                ticket.getVenue().getName(),
                ticket.getVenue().getCapacity(),
                ticket.getVenue().getType() != null ? ticket.getVenue().getType() : "не указано"
        );
    }
}