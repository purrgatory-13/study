package org.example.command;

import org.example.managers.CollectionManager;
import org.example.managers.FileManager;
import org.example.managers.OutputManager;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Команда для сохранения коллекции в файл
 */
public class SaveCommand implements Command {
    private final CollectionManager collectionManager;
    private final FileManager fileManager;
    private final OutputManager outputManager;
    private final String filename;

    /**
     * Конструктор команды сохранения
     * @param collectionManager менеджер коллекции
     * @param fileManager менеджер файлов
     * @param outputManager менеджер вывода
     * @param filename имя файла для сохранения
     */
    public SaveCommand(CollectionManager collectionManager, FileManager fileManager, OutputManager outputManager, String filename) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
        this.outputManager = outputManager;
        this.filename = filename;
    }

    /**
     * Выполняет сохранение коллекции в файл
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        try {
            fileManager.save(filename, new LinkedList<>(collectionManager.getCollection()));
            outputManager.println("Коллекция успешно сохранена в файл.");
        } catch (IOException e) {
            outputManager.printError("Ошибка при сохранении файла: " + e.getMessage());
        }
    }
}