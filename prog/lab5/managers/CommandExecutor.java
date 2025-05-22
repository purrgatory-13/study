package org.example.managers;

import org.example.command.*;
import java.util.*;

/**
 * Менеджер для выполнения команд
 */
public class CommandExecutor {
    private final Map<String, Command> commands = new HashMap<>();
    private final OutputManager outputManager;

    public CommandExecutor(CollectionManager collectionManager,
                           InputManager inputManager,
                           OutputManager outputManager) {
        this.outputManager = outputManager;
        registerCommands(collectionManager, inputManager);
    }

    private void registerCommands(CollectionManager collectionManager,
                                  InputManager inputManager) {
        // Получаем список имен команд перед созданием HelpCommand
        Collection<String> commandNames = Arrays.asList(
                "help", "info", "show", "add", "update", "remove_by_id",
                "clear", "save", "execute_script", "exit_wo_save", "head",
                "remove_head", "add_if_min", "filter_starts_with_name",
                "print_descending", "print_field_descending_refundable"
        );

        commands.put("help", new HelpCommand(outputManager, commandNames));
        commands.put("info", new InfoCommand(collectionManager, outputManager));
        commands.put("show", new ShowCommand(collectionManager, outputManager));
        commands.put("add", new AddCommand(collectionManager, inputManager, outputManager));
        commands.put("update", new UpdateCommand(collectionManager, inputManager, outputManager));
        commands.put("remove_by_id", new RemoveByIdCommand(collectionManager, outputManager));
        commands.put("clear", new ClearCommand(collectionManager, outputManager));
        commands.put("save", new SaveCommand(collectionManager, new FileManager(), outputManager, "data.json"));
        commands.put("exit_wo_save", new ExitCommand(outputManager));
        commands.put("head", new HeadCommand(collectionManager, outputManager));
        commands.put("remove_head", new RemoveHeadCommand(collectionManager, outputManager));
        commands.put("filter_starts_with_name", new FilterStartsWithNameCommand(collectionManager, outputManager));
        commands.put("print_descending", new PrintDescendingCommand(collectionManager, outputManager));
        commands.put("print_field_descending_refundable", new PrintFieldDescendingRefundableCommand(collectionManager, outputManager));
        commands.put("execute_script", new ExecuteScriptCommand(this, outputManager, inputManager));
        commands.put("add_if_min", new AddIfMinCommand(collectionManager, inputManager, outputManager));




    }

    /**
     * Выполняет команду
     * @param commandLine строка с командой и аргументами
     */
    public void execute(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return;
        }

        String[] parts = commandLine.trim().split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1].trim() : "";

        Command command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(args);
            } catch (Exception e) {
                outputManager.printError("Ошибка выполнения команды: " + e.getMessage());
            }
        } else {
            outputManager.printError("Неизвестная команда. Введите 'help' для справки.");
        }
    }

    /**
     * Возвращает список доступных команд
     */
    public Collection<String> getCommandNames() {
        return commands.keySet();
    }
}