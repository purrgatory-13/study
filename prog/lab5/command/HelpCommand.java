package org.example.command;

import org.example.managers.OutputManager;
import java.util.Collection;

/**
 * Команда вывода списка доступных команд
 */
public class HelpCommand implements Command {
    private final OutputManager outputManager;
    private final Collection<String> commandNames;

    /**
     * Создает команду помощи
     * @param outputManager менеджер вывода
     * @param commandNames коллекция имен доступных команд
     */
    public HelpCommand(OutputManager outputManager, Collection<String> commandNames) {
        this.outputManager = outputManager;
        this.commandNames = commandNames;
    }

    /**
     * Выводит список всех доступных команд
     * @param args аргумент команды (не используется)
     */
    @Override
    public void execute(String args) {
        outputManager.println("Доступные команды:");
        commandNames.forEach(cmd -> outputManager.println("- " + cmd));
    }
}