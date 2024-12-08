package ru.highloadjava.externalmemorylab.altsolutionrandommate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RandomMateListGenerator {

    public static void generateInputFile(String fileName, int nodeCount) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i <= nodeCount; i++) {
                String nextNode = (i < nodeCount) ? String.valueOf(i + 1) : "null";
                writer.write(i + " " + nextNode);
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) {
        int nodeCount = 10;  // Задаем количество узлов в списке
        String fileName = "./src/main/java/ru/highloadjava/externalmemorylab/altsolutionrandommate/input_randommate_list.txt";

        try {
            generateInputFile(fileName, nodeCount);
            System.out.println("Файл input.txt сгенерирован успешно.");
        } catch (IOException e) {
            System.err.println("Ошибка при генерации файла: " + e.getMessage());
        }
    }
}
