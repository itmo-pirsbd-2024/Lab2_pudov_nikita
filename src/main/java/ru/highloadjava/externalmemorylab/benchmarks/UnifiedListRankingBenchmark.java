package ru.highloadjava.externalmemorylab.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.highloadjava.externalmemorylab.naivesolution.ExternalMemoryListRanking;
import ru.highloadjava.externalmemorylab.altsolutionrandommate.RandomMateListGenerator;
import ru.highloadjava.externalmemorylab.altsolutionrandommate.RandomMateListRanking;
import ru.highloadjava.externalmemorylab.altsolutionwilley.WyllieListGenerator;
import ru.highloadjava.externalmemorylab.altsolutionwilley.WyllieListRanking;
import ru.highloadjava.externalmemorylab.naivesolution.DataGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 300, timeUnit = TimeUnit.MILLISECONDS)
public class UnifiedListRankingBenchmark {

    @Param({"10", "100"}) // Размер списка для тестирования
    private int size;

    private int N = 100;

    private LinkedList<Integer> list;
    private final ExternalMemoryListRanking listRanking = new ExternalMemoryListRanking();

    private final String inputExternalFilename = "./src/main/java/ru/highloadjava/externalmemorylab/naivesolution/input_external_linked_list.txt";
    private final String outputExternalFilename = "./src/main/java/ru/highloadjava/externalmemorylab/naivesolution/output_external_linked_list.txt";
    private final String inputWyllieFilename = "./src/main/java/ru/highloadjava/externalmemorylab/altsolutionwilley/input_wyllie_list.txt";
    private final String outputWyllieFilename = "./src/main/java/ru/highloadjava/externalmemorylab/altsolutionwilley/output_wyllie_list.txt";
    private final String inputRandomMateFilename = "./src/main/java/ru/highloadjava/externalmemorylab/altsolutionrandommate/input_randommate_list.txt";
    private final String outputRandomMateFilename = "./src/main/java/ru/highloadjava/externalmemorylab/altsolutionrandommate/output_randommate_list.txt";

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(UnifiedListRankingBenchmark.class.getSimpleName())
                .forks(1)
                .jvmArgs("-Xms4G", "-Xmx8G", "-XX:+UnlockDiagnosticVMOptions",
                        "-XX:+PrintCompilation", "-XX:+PrintInlining",
                        "-Djmh.stack.profiles=true")  // Включение профилирования стека
                .build();

        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void setup() {
        // Генерация данных для ExternalMemoryListRanking
        DataGenerator.generateData(inputExternalFilename, size, 1, 100);

        // Чтение данных из файла для ExternalMemoryListRanking
        list = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputExternalFilename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(Integer.parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*// Подготовка данных для ListRanking
        Random random = new Random();
        list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(random.nextInt(size)); // Генерируем случайные значения для списка
        }*/

        // Генерация данных для RandomMateListRanking
        try {
            RandomMateListGenerator.generateInputFile(inputRandomMateFilename, size);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Генерация данных для WyllieListRanking
        WyllieListGenerator.generateInputFile(inputWyllieFilename, size, 1, 100);
    }

    @TearDown(Level.Iteration)
    public void teardown() {
        System.gc();
    }

    // Бенчмарк для алгоритма ExternalMemoryListRanking
    //@Benchmark
    public void externalMemoryListRankingBenchmark(Blackhole bh) throws IOException {
        LinkedList<Integer> rankedList = new LinkedList<>();
        for (int i=0; i< N; i++) {
            rankedList = ExternalMemoryListRanking.listRanking(list);
            ExternalMemoryListRanking.writeListToFile(rankedList, outputExternalFilename);
            bh.consume(rankedList); // Используем Blackhole, чтобы избежать оптимизаций
        }
    }

    // Бенчмарк для алгоритма RandomMateListRanking
    //@Benchmark
    public void randomMateListRankingBenchmark(Blackhole bh) {
        for (int i=0; i< N; i++) {
            Map<Integer, ru.highloadjava.externalmemorylab.altsolutionrandommate.Node> nodeMap;
            try {
                nodeMap = RandomMateListRanking.readNodesFromFile(inputRandomMateFilename);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            RandomMateListRanking.pointerJumpingPhase();
            RandomMateListRanking.reconstructionPhase();

            try {
                RandomMateListRanking.writeRanksToFile(outputRandomMateFilename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bh.consume(nodeMap.values());
        }
    }

    // Бенчмарк для алгоритма WyllieListRanking
    @Benchmark
    public void wyllieListRankingBenchmark(Blackhole bh) {
        for (int i=0; i< N; i++) {
            LinkedList<ru.highloadjava.externalmemorylab.altsolutionwilley.Node> nodeList = WyllieListRanking.readListFromFile(inputWyllieFilename);
            ru.highloadjava.externalmemorylab.altsolutionwilley.Node head = nodeList.get(0);

            ConcurrentHashMap<Integer, ru.highloadjava.externalmemorylab.altsolutionwilley.Node> nodeMap = new ConcurrentHashMap<>();
            for (int j = 0; j < nodeList.size() - 1; j++) {
                nodeList.get(j).setNext(nodeList.get(j + 1));
                nodeMap.put(j, nodeList.get(j));
            }
            nodeMap.put(nodeList.size() - 1, nodeList.get(nodeList.size() - 1));

            WyllieListRanking.rankList(nodeMap, head);

            WyllieListRanking.writeListToFile(outputWyllieFilename, nodeMap.values());

            bh.consume(nodeMap.values());
        }
    }
}
