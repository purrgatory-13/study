package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.OutputManager;
import org.example.models.Ticket;

/**
 * Команда для удаления и вывода первого элемента коллекции
 */
public class RemoveHeadCommand implements Command {
    private final CollectionManager collectionManager;
    private final OutputManager outputManager;

    /**
     * Конструктор команды remove_head
     * @param collectionManager менеджер коллекции
     * @param outputManager менеджер вывода
     */
    public RemoveHeadCommand(CollectionManager collectionManager, OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.outputManager = outputManager;
    }

    /**
     * Удаляет и выводит первый элемент коллекции
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        if (collectionManager.getCollectionSize() == 0) {
            outputManager.printError("Коллекция пуста.");
            return;
        }

        Ticket removed = collectionManager.getCollection().get(0);
        collectionManager.removeById(removed.getId());
        outputManager.println("Удалён первый элемент:\n" + removed);
    }
}