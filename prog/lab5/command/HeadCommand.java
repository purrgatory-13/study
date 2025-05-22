package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.OutputManager;
import org.example.models.Ticket;

import java.util.Optional;

/**
 * Команда для вывода первого элемента коллекции
 */
public class HeadCommand implements Command {
    private final CollectionManager collectionManager;
    private final OutputManager outputManager;

    /**
     * Конструктор команды head
     * @param collectionManager менеджер коллекции
     * @param outputManager менеджер вывода
     */
    public HeadCommand(CollectionManager collectionManager, OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.outputManager = outputManager;
    }

    /**
     * Выводит первый элемент коллекции или сообщение о пустой коллекции
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        Optional<Ticket> head = collectionManager.getCollection().stream().findFirst();
        if (head.isPresent()) {
            outputManager.println("Первый элемент коллекции:\n" + head.get());
        } else {
            outputManager.printError("Коллекция пуста.");
        }
    }
}