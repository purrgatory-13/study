package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.OutputManager;

import java.util.Comparator;

/**
 * Команда для вывода элементов коллекции в порядке убывания
 */
public class PrintDescendingCommand implements Command {
    private final CollectionManager collectionManager;
    private final OutputManager outputManager;

    /**
     * Конструктор команды print_descending
     * @param collectionManager менеджер коллекции
     * @param outputManager менеджер вывода
     */
    public PrintDescendingCommand(CollectionManager collectionManager, OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.outputManager = outputManager;
    }

    /**
     * Выводит элементы коллекции в обратном порядке
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        var sorted = collectionManager.getCollection().stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        if (sorted.isEmpty()) {
            outputManager.println("Коллекция пуста.");
        } else {
            sorted.forEach(ticket -> outputManager.println(ticket.toString()));
        }
    }
}