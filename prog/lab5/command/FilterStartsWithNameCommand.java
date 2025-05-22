package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.OutputManager;

/**
 * Команда для фильтрации элементов, имя которых начинается с заданной подстроки
 */
public class FilterStartsWithNameCommand implements Command {
    private final CollectionManager collectionManager;
    private final OutputManager outputManager;

    /**
     * Создает команду фильтрации по началу имени
     * @param collectionManager менеджер коллекции
     * @param outputManager менеджер вывода
     */
    public FilterStartsWithNameCommand(CollectionManager collectionManager, OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.outputManager = outputManager;
    }

    /**
     * Выполняет фильтрацию и вывод элементов
     * @param argument подстрока для поиска в начале имен
     */
    @Override
    public void execute(String argument) {
        if (argument == null || argument.isEmpty()) {
            outputManager.printError("Необходимо указать подстроку для фильтрации.");
            return;
        }

        var result = collectionManager.getCollection().stream()
                .filter(ticket -> ticket.getName() != null && ticket.getName().startsWith(argument))
                .toList();

        if (result.isEmpty()) {
            outputManager.println("Нет элементов, имя которых начинается с: " + argument);
        } else {
            result.forEach(ticket -> outputManager.println(ticket.toString()));
        }
    }
}