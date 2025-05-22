package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.OutputManager;

/**
 * Команда для удаления элемента коллекции по ID
 */
public class RemoveByIdCommand implements Command {
    private final CollectionManager collectionManager;
    private final OutputManager outputManager;

    /**
     * Конструктор команды remove_by_id
     * @param collectionManager менеджер коллекции
     * @param outputManager менеджер вывода
     */
    public RemoveByIdCommand(CollectionManager collectionManager, OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.outputManager = outputManager;
    }

    /**
     * Удаляет элемент с указанным ID из коллекции
     * @param argument строка содержащая ID элемента для удаления
     */
    @Override
    public void execute(String argument) {
        try {
            long id = Long.parseLong(argument);
            boolean removed = collectionManager.removeById(id);
            if (removed) {
                outputManager.println("Билет с ID " + id + " был удалён.");
            } else {
                outputManager.printError("Билет с таким ID не найден.");
            }
        } catch (NumberFormatException e) {
            outputManager.printError("ID должен быть числом.");
        }
    }
}