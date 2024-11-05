package ru.highloadjava.externalmemorylab.altsolutionrandommate;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.Map;

public class RandomMateListRanking {

    private static final Map<Integer, Node> nodes = new ConcurrentHashMap<>();
    private static AtomicInteger t = new AtomicInteger(1);

    public static void main(String[] args) throws IOException {
        // Считывание данных из файла и выполнение алгоритма ранжирования
        readNodesFromFile("./src/main/java/ru/highloadjava/externalmemorylab/altsolutionrandommate/input_randommate_list.txt");
        pointerJumpingPhase();
        reconstructionPhase();
        writeRanksToFile("./src/main/java/ru/highloadjava/externalmemorylab/altsolutionrandommate/output_randommate_list.txt");
    }

    /**Формат строки: <id> <next_id>, где <next_id> может быть null (для последнего узла).
    Метод создает новый объект Node для каждого узла, устанавливая его id и ссылку на следующий узел (next), и добавляет его в nodes.*/
    public static Map<Integer, Node> readNodesFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                int id = Integer.parseInt(parts[0]);
                Integer next = parts[1].equals("null") ? null : Integer.parseInt(parts[1]);
                nodes.put(id, new Node(id, next));
            }
        }
        return nodes;
    }

    /**Этот метод реализует основную фазу "прыжков указателей" (pointer jumping), где происходит случайное спаривание узлов.
     Проверка активности: Алгоритм продолжает выполнение, пока существуют активные узлы с ненулевым next.
     Случайное назначение пола: Для каждого узла и его соседа случайным образом назначается пол (M или F).
     Спаривание узлов: Если текущий узел (node) женского пола (F), а следующий узел мужского пола (M), происходит "спаривание":
     Ранг следующего узла добавляется к рангу текущего узла.
     Текущий узел обновляет ссылку на next, чтобы перепрыгнуть через спаренный узел.
     Следующий узел становится неактивным, и его time фиксируется.
     Обновление времени: После каждой итерации t увеличивается, чтобы отслеживать текущий шаг.*/
    public static void pointerJumpingPhase() {
        while (nodes.values().stream().anyMatch(node -> node.isActive() && node.getNext() != null)) {
            nodes.values().parallelStream().forEach(node -> {
                if (node.isActive() && node.getNext() != null) {
                    // Определение пола узла случайным образом
                    node.setSex(Math.random() < 0.5 ? "M" : "F");
                    Node nextNode = nodes.get(node.getNext());
                    if (nextNode != null) {
                        nextNode.setSex(Math.random() < 0.5 ? "M" : "F");
                        if ("F".equals(node.getSex()) && "M".equals(nextNode.getSex())) {
                            // Обновление ранга и указателя
                            nextNode.setTime(t.get());
                            node.setActive(false);
                            node.incrementRank(nextNode.getRank());
                            node.setNext(nextNode.getNext());
                        }
                    }
                }
            });
            t.incrementAndGet();
        }
    }

    /**Фаза реконструкции выполняется после завершения pointerJumpingPhase.
    Алгоритм проходит узлы в порядке убывания значений time, которые фиксировались во время "спаривания".
    Для каждого узла с текущим временем, если у него есть следующий узел (next), ранг этого узла прибавляется к его текущему рангу.
    Процесс повторяется, пока maxTime не станет равен нулю.*/
    public static void reconstructionPhase() {
        int maxTime = t.get() - 1;
        while (maxTime > 0) {
            int finalMaxTime = maxTime;
            nodes.values().parallelStream()
                    .filter(node -> node.getTime() != null && node.getTime() == finalMaxTime && node.getNext() != null)
                    .forEach(node -> {
                        Node nextNode = nodes.get(node.getNext());
                        if (nextNode != null) {
                            node.incrementRank(nextNode.getRank());
                        }
                    });
            maxTime--;
        }
    }

    public static void writeRanksToFile(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Node node : nodes.values()) {
                writer.write("Node " + node.getId() + ": Rank = " + node.getRank() + "\n");
            }
        }
    }
}
