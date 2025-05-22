package org.example.command;

import org.example.managers.CommandExecutor;
import org.example.managers.InputManager;
import org.example.managers.OutputManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Команда для выполнения скрипта из файла без риска циклической рекурсии.
 */
public class ExecuteScriptCommand implements Command {

    /** Набор скриптов, которые уже выполняются (канонические пути). */
    private static final Set<String> runningScripts = new HashSet<>();

    private final CommandExecutor commandExecutor;
    private final OutputManager outputManager;
    private final InputManager inputManager;

    public ExecuteScriptCommand(CommandExecutor commandExecutor,
                                OutputManager outputManager,
                                InputManager inputManager) {
        this.commandExecutor = commandExecutor;
        this.outputManager  = outputManager;
        this.inputManager   = inputManager;
    }

    @Override
    public void execute(String argument) {
        if (argument == null || argument.isBlank()) {
            outputManager.printError("Укажите имя файла скрипта: execute_script <файл>");
            return;
        }

        String canonicalPath;
        File scriptFile = new File(argument);
        try {
            canonicalPath = scriptFile.getCanonicalPath();
        } catch (IOException e) {
            outputManager.printError("Ошибка определения пути файла: " + e.getMessage());
            return;
        }

        if (!scriptFile.exists() || !scriptFile.canRead()) {
            outputManager.printError("Файл не найден или недоступен: " + canonicalPath);
            return;
        }

        if (runningScripts.contains(canonicalPath)) {
            outputManager.printError("Обнаружена рекурсивная попытка запустить скрипт: " + canonicalPath);
            return;
        }

        runningScripts.add(canonicalPath);
        try {
            inputManager.pushInputFromFile(canonicalPath);

            String line;
            while ((line = inputManager.readWithPrompt("")) != null) {
                if (line.isBlank()) continue;
                outputManager.println("> " + line);
                commandExecutor.execute(line);
            }

            inputManager.popInput();
        } catch (FileNotFoundException e) {
            outputManager.printError("Не удалось открыть файл: " + e.getMessage());
        } finally {
            runningScripts.remove(canonicalPath);
        }
    }
}
