package ru.highloadjava.externalmemorylab.altsolutionwilley;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class WyllieListGenerator {

    public static void generateInputFile(String filename, int nodeCount, int minValue, int maxValue) {
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < nodeCount; i++) {
                int value = random.nextInt(maxValue - minValue + 1) + minValue;
                writer.write(Integer.toString(value));
                writer.newLine();
            }
            System.out.println("Файл " + filename + " успешно создан с " + nodeCount + " узлами.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Параметры генерации
        String filename = "./src/main/java/ru/highloadjava/externalmemorylab/altsolutionwilley/input_wyllie_list.txt";
        int nodeCount = 10; // Количество узлов в списке
        int minValue = 1;   // Минимальное значение узла
        int maxValue = 100; // Максимальное значение узла

        // Генерация файла
        generateInputFile(filename, nodeCount, minValue, maxValue);
    }
}
