package org.example.managers;

/**
 * Менеджер для вывода информации
 */
public class OutputManager {
    /**
     * Выводит сообщение
     */
    public void print(String message) {
        System.out.print(message);
    }

    /**
     * Выводит сообщение с переносом строки
     */
    public void println(String message) {
        System.out.println(message);
    }

    /**
     * Выводит ошибку
     */
    public void printError(String error) {
        System.err.println("Ошибка: " + error);
    }
}