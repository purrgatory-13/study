package org.example.command;

import org.example.managers.OutputManager;

/**
 * Команда для завершения работы программы
 * <p>
 * При выполнении немедленно завершает работу приложения
 * без сохранения текущего состояния коллекции
 */
public class ExitCommand implements Command {
    private final OutputManager outputManager;

    /**
     * Создает команду выхода
     * @param outputManager менеджер для вывода сообщений
     */
    public ExitCommand(OutputManager outputManager) {
        this.outputManager = outputManager;
    }

    /**
     * Выполняет завершение программы
     * @param argument игнорируется (не требуется)
     */
    @Override
    public void execute(String argument) {
        outputManager.println("Завершение программы без сохранения...");
        System.exit(0);
    }
}