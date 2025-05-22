package org.example.managers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.example.models.Ticket;

import java.io.*;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.LinkedList;

/**
 * Менеджер для работы с файлами (загрузка/сохранение коллекции)
 */
public class FileManager {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    /**
     * Загружает коллекцию из JSON-файла
     * @param filename имя файла для загрузки
     * @return загруженная коллекция билетов
     * @throws IOException при ошибках чтения файла
     */
    public LinkedList<Ticket> load(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) {
            return new LinkedList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type collectionType = new TypeToken<LinkedList<Ticket>>(){}.getType();
            LinkedList<Ticket> result = gson.fromJson(reader, collectionType);
            return result != null ? result : new LinkedList<>();
        }
    }

    /**
     * Сохраняет коллекцию в JSON-файл
     * @param filename имя файла для сохранения
     * @param collection коллекция для сохранения
     * @throws IOException при ошибках записи в файл
     */
    public void save(String filename, LinkedList<Ticket> collection) throws IOException {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(collection, writer);
        }
    }
}