package org.example.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

/**
 * Менеджер для обработки ввода из разных источников
 */
public class InputManager {
    private final Stack<Scanner> inputStack = new Stack<>();

    /**
     * Создает менеджер ввода с консольным вводом по умолчанию
     */
    public InputManager() {
        inputStack.push(new Scanner(System.in));
    }

    /**
     * Добавляет файл как источник ввода
     * @param filename путь к файлу
     * @throws FileNotFoundException если файл не найден
     */
    public void pushInputFromFile(String filename) throws FileNotFoundException {
        inputStack.push(new Scanner(new File(filename)));
    }

    /**
     * Возвращается к предыдущему источнику ввода
     */
    public void popInput() {
        if (inputStack.size() > 1) {
            inputStack.pop();
        }
    }

    /**
     * Читает строку с приглашением к вводу
     * @param prompt приглашение для пользователя
     * @return введенная строка
     */
    public String readWithPrompt(String prompt) {
        while (true) {
            System.out.print(prompt);
            Scanner current = inputStack.peek();
            if (current.hasNextLine()) {
                return current.nextLine().trim();
            } else {
                if (inputStack.size() > 1) {
                    popInput();
                    continue;
                } else {

                    return current.nextLine().trim();
                }
            }
        }
    }

    /**
     * Читает целое число с обработкой ошибок
     * @param prompt приглашение для пользователя
     * @param errorMessage сообщение при ошибке ввода
     * @return введенное число
     */
    public int readInt(String prompt, String errorMessage) {
        while (true) {
            try {
                return Integer.parseInt(readWithPrompt(prompt));
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            }
        }
    }
}