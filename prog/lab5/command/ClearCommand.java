package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.OutputManager;

/**
 * Команда для очистки коллекции
 * <p>
 * Удаляет все элементы из текущей коллекции без возможности восстановления.
 * После выполнения команды коллекция становится пустой.
 */
public class ClearCommand implements Command {
    private final CollectionManager collectionManager;
    private final OutputManager outputManager;

    /**
     * Создает команду очистки коллекции
     * @param collectionManager менеджер коллекции для очистки
     * @param outputManager менеджер для вывода результата операции
     */
    public ClearCommand(CollectionManager collectionManager, OutputManager outputManager) {
        this.collectionManager = collectionManager;
        this.outputManager = outputManager;
    }

    /**
     * Выполняет очистку коллекции
     * @param argument игнорируется (не требуется для выполнения команды)
     */
    @Override
    public void execute(String argument) {
        collectionManager.clear();
        outputManager.println("Коллекция очищена.");
    }
}