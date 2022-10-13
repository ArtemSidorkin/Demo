# Многопоточность

Многопоточность - выполнение участков кода без определенного порядка.

Нужна для ускорения вычислений на многопроцессорных(многоядерных) системах,
а также для обработки синхронных IO операций, без остановки выполнения остальных частей программы.
Например, Desktop-приложение может загружать что-то по сети используя синхронное IO и при этом не блокироваться.

Потоки в Java управляются операционной системы, являются частью процесса и общую с ним память.
В новых релизах ожидаются легковесные зеленые потоки, которыми управляет JVM.

## Простейший способ запуска нового потока

    public class MultithreadingMain
    {
        public static void main(String[] args)
        {
            new Thread(() -> System.out.println("Hello world from another thread!")).start();
        }
    }

## Блокировка

    public class MultithreadingMain
    {
        private static int counter = 0;
    
        public static void main(String[] args)
        {
            new Thread(
                    () ->
                    {
                        for (int i = 0; i < 100000; i++) counter++;
    
                        System.out.println(counter);
                    }
            )
                    .start();
    
            for (int i = 0; i < 100000; i++) counter++;
    
            System.out.println(counter);
        }
    }

Результат непредсказуем, например:

    > Task :MultithreadingMain.main()
    109414
    131460

А теперь добавим блокировку

    public class MultithreadingMain
    {
        private static int counter = 0;
        private static final Object lock = new Object();
    
        public static void main(String[] args)
        {
            new Thread(
                    () ->
                    {
                        synchronized (lock)
                        {
                            for (int i = 0; i < 100000; i++) counter++;
                        }
    
                        System.out.println(counter);
                    }
            )
                    .start();
    
            synchronized (lock)
            {
                for (int i = 0; i < 100000; i++) counter++;
            }
    
            System.out.println(counter);
        }
    }    

Конечнй результат теперь стабилен, последний завершившийся поток выдаст ожидаемое число:

    > Task :MultithreadingMain.main()
    100598
    200000

Однако, пример искусственный, не делайте так

### Deadlock

Возникает когда поток захватывает блокировку над несколькими объектами,
а в то же время другой поток так же захватывает блокировку над теме же объектами, но в другом порядке.
Как избежать: bметь строгий порядой блокировок:

Thread 1:

    synchronized(a)
    {
        ...
        synchronized(b)
        {
            ...
            synchronized(c)
            {
                ...
            }
            ...
        }
        ...
    }

Thread 2:

    synchronized(a)
    {
        ...
        synchronized(b)
        {
            ...
            synchronized(c)
            {
                ...
            }
            ...
        }
        ...
    }

Thread 3:

    synchronized(a)
    {
        ...
        synchronized(b)
        {
            ...
        }
        ...
    }

Thread 4:

    synchronized(a)
    {
        ...
        synchronized(c)
        {
            ...
        }
        ...
    }

## volatile

Данная конструкция гарантирует атомарное чтение и запись в поле

### Double Check Singleton

    public class Singleton
    {
        private static volatile Singleton instance;
    
        public static Singleton getInstance()
        {
            if (instance == null)
                synchronized (Singleton.class)
                {
                    if (instance == null) instance = new Singleton();
                }
    
            return instance;
        }
    }

Правда такое нужно использовать только при инициализации с параметрами, в противном случае лучше использовать:

    public class Singleton
    {
        private static final Singleton instance = new Singleton();
    }

## Связь между потоками

Связь потоков осуществляется через методы объекта блокировки, внутри блока сериализации.

- wait - поток засыпает и ожидает пробуждения от других потоков
- notifyAll() - пробуждает все потоки в режиме ожидания
- notify() - пробуждает только один поток

Но Данные примитивы на практике обычно не используются, ввиду существования более высокоуровневых инструментов

## Пулы потоков

Зачастую удобно не создавать потоки, брать их из пула:

    public class MultithreadingMain
    {
        public static void main(String[] args)
        {
            ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    
            for (int i = 0; i < 10; i++) cachedThreadPool.submit(() -> System.out.println(Thread.currentThread().getId()));
        }
    }

    > Task :MultithreadingMain.main()
    14
    18
    17
    16
    13
    15
    19
    20
    21
    22

Слишком большое число потоков может положить программу, поэтому лучше использовать фиксированный пул

    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 100);

### Fork Join пул

Данный пул существует неявно, используется в основном для вычислений, для IO не эффективен, так как равен 

    Runtime.getRuntime().availableProcessors() * 2

## Полезные утилиты

### Atomics

Атомики позволяют забыть о блокировках при операциях над некоторыми данными

    public class MultithreadingMain
    {
        private static final AtomicLong counter = new AtomicLong();
    
        public static void main(String[] args)
        {
            ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    
            for (int i = 0; i < 10; i++)
                cachedThreadPool
                        .submit(() ->
                        {
                            for (int j = 0; j < 10000; j++) counter.incrementAndGet();
    
                            System.out.println(counter.get());
                        });
        }
    }

Финальный результат как и ожидался:

    > Task :MultithreadingMain.main()
    68680
    70585
    70030
    80000
    90000
    69820
    69003
    68888
    68149
    100000

### Синхронизированные коллекции

    List<String> syncList = Collections.synchronizedList(new ArrayList<>());
    Set<String> syncSet = Collections.synchronizedSet(new HashSet<>());
    Map<String, String> syncMap = Collections.synchronizedMap(new HashMap<>());

Данные коллекции потокобезопасны(методы синхронизированы), хотя и не очень эффективны 

### Эффективные многопоточные коллекции

    ConcurrentHashMap<String, String> concurrentMap = new ConcurrentHashMap<>();
    ConcurrentLinkedQueue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedDeque<String> concurrentDeque = new ConcurrentLinkedDeque<>();

Данные коллекции реализуют алгоритмы,
которые позволяют не блокировать некоторые методы или иметь короткие/частичные блокировки

### CopyOnWriteArrayList

    List<String> copyOnWriteList = new CopyOnWriteArrayList<>();

Чтение - не блокируется, при записи внутри создается копия элементов с изменениями,
поэтому эффективно использовать при подавляющем чтении

### CountdownLatch and CyclicBarrier - синхронизация потоков

    public class MultithreadingMain
    {
        private static final AtomicLong counter = new AtomicLong();
    
        public static void main(String[] args) throws InterruptedException
        {
            ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    
            CountDownLatch countDownLatch = new CountDownLatch(10);
    
            for (int i = 0; i < 10; i++)
                cachedThreadPool
                        .submit(() ->
                        {
                            for (int j = 0; j < 10000; j++) counter.incrementAndGet();
    
                            countDownLatch.countDown();
                        });
    
            countDownLatch.await();
    
            System.out.println(counter.get());
    
            cachedThreadPool.shutdown();
        }
    }

CountdownLatch - может инициировать ожидание 1 раз. CyclicBarrier - не ограничено.

### Completable future - инкапсулированное вычисление в отдельном потоке

Запускаем задачи в разных потоках и объединяем в результат

    public class MultithreadingMain
    {
        public static void main(String[] args) throws InterruptedException, ExecutionException
        {
            List<CompletableFuture<Long>> completableFutures = new ArrayList<>();
    
            for (int i = 0; i < 10; i++)
                completableFutures
                    .add(
                        CompletableFuture
                            .supplyAsync(() ->
                            {
                                long sum = 0;
    
                                for (long j = 1; j <= 10000; j++) sum = j;
    
                                return sum;
                            })
                    );
    
            long sum = 0;
    
            for (CompletableFuture<Long> completableFuture : completableFutures) sum += completableFuture.get();
    
            System.out.println(sum);
        }
    }

результат

    > Task :MultithreadingMain.main()
    100000

Или можно создать futures, и ждать пока кто-то их выполнит

    public class MultithreadingMain
    {
        public static void main(String[] args) throws InterruptedException, ExecutionException
        {
            List<CompletableFuture<Long>> completableFutures = new ArrayList<>();
    
            for (int i = 0; i < 10; i++) completableFutures.add(new CompletableFuture<>());
    
            new Thread(() -> completableFutures.forEach(cf -> cf.complete(1L))).start();
    
            long sum = 0;
    
            for (CompletableFuture<Long> completableFuture : completableFutures) sum += completableFuture.get();
    
            System.out.println(sum);
        }
    }

результат

    > Task :MultithreadingMain.main()
    10

Есть, еще возможность передавать callback на завершение

    public class MultithreadingMain
    {
        public static void main(String[] args)
        {
            CompletableFuture
                    .supplyAsync(() -> {System.out.println(Thread.currentThread().getId()); return null;})
                    .whenComplete((nothing, throwable) -> System.out.println(Thread.currentThread().getId()));
        }
    }

Пример результата

    > Task :MultithreadingMain.main()
    13
    1

**Внимание**: используется Fork Join Pool, но можно передать и другой.

## Рекомендуемая литература

 - The Art of Multiprocessor Programming 2nd Edition (by Maurice Herlihy, Nir Shavit, Victor Luchangco, Michael Spear)
 - Concurrent Programming in Java : Design Principles and Pattern, 2nd Edition
 - habr.com статьи на тему многопоточность, смотрите так же рейтинг и комментарии