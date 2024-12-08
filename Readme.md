# Lab2 External Data Algorithms

В качестве задания была выбрана задача ListRanking 
(Необходимо для каждого элемента определить, каким он является по счету с конца списка. Расстояние до конца списка будем называть рангом элемента)

Для решения были написаны наивная реализация, алгоритм Wyllie 
и алгоритм рандмоизированного состояния RandomState algorithm.

Для профилирования использовался async-profiler. Для работы с файлами и потоковой обработки данных использовались Streams.

Kод основных методов в каждом из алгоритмов можно найти в соответствующем пакете. Более подробная информация находится в исходном коде.
Для каждого из алгоритмов были написаны соответвующие генераторы данных в файлы в классах 
DataGenerator, WyllieListGenerator, RandomMateListGenerator.



## Бенчмарк
Перейдем к результатам работы наших алгоритмов по времени, которые получились при помощи  JMH.

Результаты бенчмарка

![result_benchmark_lab2.png](assets/result_benchmark_lab2.png)

Как видно из результатов наилучших результатов на наших жанных добился алгоритм Willey.

Осуществим профилирование бенчмарка и выведем flamegraph для каждого алгоритма

На flamegraph для наивной реализации видно, что происходит вызов метода listRanking, а уже над ним в стеке вызовов
параллельная обработка

![ExternalBenchmark_flamegraph.png](assets/ExternalBenchmark_flamegraph.png)

Для алгоритма RandomMate на flamegraph также видно, что сначала происходит вызов метода для ранжирования списка
reconstructionPhase и уже затем потоковая обработка внутри метода

![RandomMateBenchmark_flamegraph.png](assets/RandomMateBenchmark_flamegraph.png)

Для алгоритма Wyllie на flamegraph видно, что вызывается метод rankList, а в нем при ранжировании выполняется
потоковая обработка (свертка reduce)

![WyllieBenchmark_flamegraph.png](assets/WyllieBenchmark_flamegraph.png)

Рассмотрим несколько примеров JIT компиляции нащих алгоритмов

Первым рассмотрим алгоритм с наивной релаизацией. 

На первом фрагменте видно, что в процессе выполнения 
алгоритма возникает механизм блокировки и его разрешение при помощи ReentrantLock. Это неудивительно, поскольку
мы используем многопоточную обработку при ранжировании списка

![externalBenchmark_JIT_1.png](assets/externalBenchmark_JIT_1.png)

На другом фрагменте мы можем видеть механизм записи данных в некий файл

![externalBenchmark_JIT_2.png](assets/externalBenchmark_JIT_2.png)

Перейдем теперь к алгоритму RandomMate

В примере показано как для данных создается копия массива

![RandomMate_Benchmark_JIT_1.png](assets/RandomMate_Benchmark_JIT_1.png)

В следующем примере показан последовательный вызов методов ранжирования в методе pointerJumpingPhase

![RandomMate_Benchmark_JIT_2.png](assets/RandomMate_Benchmark_JIT_2.png)

Что касается алгоритма Willey, то в данном фрагменте представлен механизм записи данных файл и использования
многопоточного ассоциативного массива concurrencyHashMap. Также видно как в конце операции снимается блокировка

![Wyllie_Benchmark_JIT_1.png](assets/Wyllie_Benchmark_JIT_1.png)

Кроме того, в качестве примера продемонстрируем как в JIT представлено механизм свертки reduce при 
потоковом ранжировании списка поскольку мы используем библиотеку Streams.

![Wyllie_Benchmark_JIT_2.png](assets/Wyllie_Benchmark_JIT_2.png)



