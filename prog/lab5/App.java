package org.example;

import org.example.managers.*;
import org.example.models.Ticket;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Главный класс приложения для управления коллекцией билетов.
 * <p>
 * Приложение позволяет:
 * <ul>
 *     <li>Загружать коллекцию билетов из JSON-файла</li>
 *     <li>Выполнять команды для управления коллекцией</li>
 *     <li>Сохранять изменения обратно в файл</li>
 * </ul>
 *
 * @author Lilia
 * @version 1.0
 */
public class App {
    /**
     * Точка входа в приложение.
     * <p>
     * Загружает данные из файла (если указан аргумент командной строки, иначе использует "data.json"),
     * позволяет пользователю вводить команды для управления коллекцией, сохраняет изменения при выходе.
     *
     * @param args Аргументы командной строки (первый аргумент — путь к файлу данных).
     */
    public static void main(String[] args) {
        String dataFile = args.length > 0 ? args[0] : "data.json";

        CollectionManager collectionManager = new CollectionManager();
        FileManager fileManager = new FileManager();
        InputManager inputManager = new InputManager();
        OutputManager outputManager = new OutputManager();
        CommandExecutor commandExecutor = new CommandExecutor(collectionManager, inputManager, outputManager);

        // Загрузка данных
        try {
            LinkedList<Ticket> loadedTickets = fileManager.load(dataFile);
            loadedTickets.forEach(collectionManager::add);
            outputManager.println("Загружено " + loadedTickets.size() + " билетов из файла " + dataFile);
        } catch (IOException e) {
            outputManager.printError("Ошибка загрузки: " + e.getMessage());
            outputManager.println("Работаем с пустой коллекцией");
        }

        // Основной цикл ввода команд
        outputManager.println("Введите команду (help - справка):");
        while (true) {
            String input = inputManager.readWithPrompt("> ");
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            commandExecutor.execute(input);
        }

        // Сохранение данных перед выходом
        try {
            fileManager.save(dataFile, new LinkedList<>(collectionManager.getCollection()));
            outputManager.println("Данные сохранены в " + dataFile);
        } catch (IOException e) {
            outputManager.printError("Ошибка сохранения: " + e.getMessage());
        }
    }
}