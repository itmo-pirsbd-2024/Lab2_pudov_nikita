package ru.highloadjava.externalmemorylab.naivesolution;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExternalMemoryListRanking {

    // Метод для чтения данных из файла
    public static LinkedList<Integer> readListFromFile(String filePath) throws IOException {
        return (LinkedList<Integer>) Files.lines(Paths.get(filePath))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    // Метод для записи результатов в файл
    public static void writeListToFile(List<Integer> list, String filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (Integer value : list) {
                writer.write(value.toString());
                writer.newLine();
            }
        }
    }

    /**Метод для ранжирования списка
     ranks — это список для хранения рангов каждого элемента; он инициализируется значением -1 для всех позиций.
     updated отслеживает, произошло ли какое-либо обновление значений.
     step задает текущий шаг для обновления индексов в каждом проходе.
     newRanks хранит промежуточные обновления значений рангов на каждой итерации.
     finalStep и finalRanks — локальные копии для работы в параллельном потоке.
     Использование IntStream.range(0, n).parallel() обрабатывает каждый элемент списка параллельно.
     На каждой итерации для элемента i:

     Если i >= finalStep, newRanks[i] получает значение finalRanks[i - step] + 1.
     Иначе newRanks[i] получает то же значение, что и finalRanks[i], поскольку для этого элемента ранг не меняется на данной итерации.
     for-цикл проверяет, произошло ли обновление значений рангов на текущей итерации. Если обнаружено изменение, переменная updated устанавливается в true, и цикл do-while повторяется.
     step *= 2 удваивает шаг, чтобы ускорить процесс ранжирования, пока список не стабилизируется.*/
    public static LinkedList<Integer> listRanking(List<Integer> list) {
        int n = list.size();
        LinkedList<Integer> ranks = new LinkedList<>(Collections.nCopies(n, -1));

        // Итерация 1: Расчет начальных значений
        for (int i = 0; i < n; i++) {
            ranks.set(i, list.get(i));
        }

        boolean updated;
        int step = 1;

        do {
            updated = false;
            LinkedList<Integer> newRanks = new LinkedList<>(Collections.nCopies(n, 0));

            // Используем параллельные потоки для обработки списка
            int finalStep = step;
            LinkedList<Integer> finalRanks = ranks;
            IntStream.range(0, n).parallel().forEach(i -> {
                if (i >= finalStep) {
                    newRanks.set(i, finalRanks.get(i - finalStep) + 1);
                } else {
                    newRanks.set(i, finalRanks.get(i));
                }
            });

            // Проверяем, были ли обновлены значения
            for (int i = 0; i < n; i++) {
                if (!newRanks.get(i).equals(ranks.get(i))) {
                    updated = true;
                    break;
                }
            }

            ranks = newRanks;
            step *= 2; // Увеличиваем шаг
        } while (updated);

        return ranks;
    }

    public static void main(String[] args) {
        String inputFilePath = "./src/main/java/ru/highloadjava/externalmemorylab/naivesolution/input_external_linked_list.txt";  // Путь к файлу с входными данными
        String outputFilePath = "./src/main/java/ru/highloadjava/externalmemorylab/naivesolution/output_external_linked_list.txt"; // Путь к файлу для записи результатов

        try {
            // Читаем входные данные из файла
            LinkedList<Integer> inputList = readListFromFile(inputFilePath);
            System.out.println("Входные данные: " + inputList);

            // Выполняем ранжирование
            LinkedList<Integer> rankedList = listRanking(inputList);
            System.out.println("Ранжированный список: " + rankedList);

            // Записываем результаты в файл
            writeListToFile(rankedList, outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
