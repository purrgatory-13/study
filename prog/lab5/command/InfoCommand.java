package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.OutputManager;
import java.time.format.DateTimeFormatter;

/**
 * Команда для вывода информации о коллекции
 */
public class InfoCommand implements Command {
    private final CollectionManager collectionManager;
    private final OutputManager outputManager;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public InfoCommand(CollectionManager collectionManager, OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.outputManager = outputManager;
    }

    @Override
    public void execute(String args) {
        String info = String.format(
                "Информация о коллекции:\n" +
                        "  Тип: %s\n" +
                        "  Дата инициализации: %s\n" +
                        "  Количество элементов: %d",
                collectionManager.getCollectionType(),
                collectionManager.getInitDate().format(DATE_FORMATTER),
                collectionManager.getCollectionSize()
        );
        outputManager.println(info);
    }
}