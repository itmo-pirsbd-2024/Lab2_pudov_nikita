package ru.highloadjava.externalmemorylab.altsolutionwilley;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;



public class WyllieListRanking {

    public static void main(String[] args) {
        // Считываем список из файла
        LinkedList<Node> nodeList = readListFromFile("./src/main/java/ru/highloadjava/externalmemorylab/altsolutionwilley/input_wyllie_list.txt");

        // Построение связного списка из массива
        ConcurrentHashMap<Integer, Node> nodeMap = new ConcurrentHashMap<>();
        Node head = nodeList.get(0);
        for (int i = 0; i < nodeList.size() - 1; i++) {
            nodeList.get(i).setNext(nodeList.get(i + 1));
            nodeMap.put(i, nodeList.get(i));
        }
        nodeMap.put(nodeList.size() - 1, nodeList.get(nodeList.size() - 1));

        // Выполнение алгоритма Wyllie для ранжирования
        rankList(nodeMap, head);

        // Запись результата в файл
        writeListToFile("./src/main/java/ru/highloadjava/externalmemorylab/altsolutionwilley/output_wyllie_list.txt", nodeMap.values());
    }

    /** Функция для выполнения алгоритма Wyllie.
     Создаёт AtomicInteger updatedNodes, чтобы отслеживать количество узлов, которые нужно обновить.
     Пока количество узлов для обновления больше нуля:
        Параллельно для каждого узла проверяется, имеет ли он следующий узел (node.getNext() != null).
     Если следующий узел существует, то: Ранг текущего узла увеличивается на ранг следующего (node.rank += node.getNext().rank).
     Ссылка next обновляется на узел через один (node.setNext(node.getNext().getNext())), чтобы ускорить процесс.
     Если обновление произошло, счётчик updatedNodes увеличивается.
     Цикл продолжается, пока updatedNodes не станет равным нулю, что означает завершение ранжирования для всех узлов.*/
    public static void rankList(ConcurrentHashMap<Integer, Node> nodeMap, Node head) {
        AtomicInteger updatedNodes = new AtomicInteger(nodeMap.size());

        while (updatedNodes.get() > 0) {
            // Параллельное обновление рангов
            updatedNodes.set((int) nodeMap.values().stream().parallel().filter(node -> {
                if (node.getNext() != null) {
                    node.rank += node.getNext().rank;
                    node.setNext(node.getNext().getNext());
                    return true;
                }
                return false;
            }).count());
        }
    }

    // Чтение связного списка из файла
    public static LinkedList<Node> readListFromFile(String filename) {
        LinkedList<Node> nodeList = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                int value = Integer.parseInt(line.trim());
                nodeList.add(new Node(value));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodeList;
    }

    // Запись связного списка с рангами в файл
    public static void writeListToFile(String filename, Collection<Node> nodes) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Node node : nodes) {
                bw.write("Value: " + node.getValue() + ", Rank: " + node.rank + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
