package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.OutputManager;

/**
 * Команда для вывода значений поля refundable в порядке убывания
 */
public class PrintFieldDescendingRefundableCommand implements Command {
    private final CollectionManager collectionManager;
    private final OutputManager outputManager;

    /**
     * Создает команду для вывода значений refundable
     * @param collectionManager менеджер коллекции
     * @param outputManager менеджер вывода
     */
    public PrintFieldDescendingRefundableCommand(CollectionManager collectionManager, OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.outputManager = outputManager;
    }

    /**
     * Выполняет вывод значений поля refundable (от true к false)
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        var sortedRefundables = collectionManager.getCollection().stream()
                .map(ticket -> ticket.getRefundable() != null ? ticket.getRefundable() : Boolean.FALSE)
                .sorted((a, b) -> Boolean.compare(b, a))
                .toList();

        if (sortedRefundables.isEmpty()) {
            outputManager.println("Нет значений поля refundable.");
        } else {
            sortedRefundables.forEach(refundable -> outputManager.println(refundable.toString()));
        }
    }
}