package ru.highloadjava.externalmemorylab.naivesolution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {

    public static void main(String[] args) {
        String filePath = "./src/main/java/ru/highloadjava/externalmemorylab/naivesolution/input_external_linked_list.txt"; // Путь к файлу для записи данных
        int size = 100; // Количество элементов
        int minValue = 1; // Минимальное значение
        int maxValue = 100; // Максимальное значение

        generateData(filePath, size, minValue, maxValue);
    }

    // Метод для генерации случайных данных и записи их в файл
    public static void generateData(String filePath, int size, int minValue, int maxValue) {
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < size; i++) {
                int randomValue = random.nextInt((maxValue - minValue) + 1) + minValue;
                writer.write(String.valueOf(randomValue));
                writer.newLine();
            }
            System.out.println("Данные успешно сгенерированы и записаны в " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }
}
