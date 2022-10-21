# Производительность

## Общие рекомендации

- Включите G1 сборщик мусора -XX:+UseG1GC (по-умолчанию с java 9)
- Включите tired compilation -XX:+TieredCompilation (по-умолчанию с java 8)
- Выделите достаточное количество памяти -Xmx4g (данная команда к примеру выделяет 4 гигабайта)
- Настройте томкат, если используете Spring Boot, самое важное потоки server.tomcat.threads.max=1000 (базовое руководство по общей настройке https://www.baeldung.com/spring-boot-configure-tomcat)
- Старайтесь адекватно оценивать сложность алгоритмов и не использовать решение "В лоб". Например, вместо пузырьковой сортировки, стоит использовать сортировку слиянием или быструю сортировку. После получения решения "в лоб", старайтесь оптимизировать (рекомендую Gayle Laakmann McDowell: Cracking the Coding Interview: 189 Programming Questions and Solutions 6th Edition)

## Базовая оценка производительности

В большинстве базовых сценариев достаточно простых замеров временных промежутков, однако, есть подвох. Рассмотрим производительность пузырьковой сортировки:

    public class Main
    {
        private static final Random random = new Random();
    
        public static void main(String[] args)
        {
            long t1 = currentTimeMillis();
    
            sort(generateRandomArray());
    
            System.out.println(currentTimeMillis() - t1);
        }
    
        private static int[] generateRandomArray()
        {
            int[] array = new int[3000];
    
            for (int i = 0; i < array.length; i++) array[i] = random.nextInt();
    
            return array;
        }
    
        private static void sort(int[] array)
        {
            for (int i = 0; i < array.length; i++)
                for (int j = i + 1; j < array.length; j++)
                    if (array[i] > array[j])
                    {
                        int temp = array[i];
                        array[i] = array[j];
                        array[j] = temp;
                    }
        }
    }

_

10 мс

Теперь добавим небольшой прогрев

    for (int i = 0; i < 100; i++) sort(generateRandomArray());

    long t1 = currentTimeMillis();

    sort(generateRandomArray());

    System.out.println(currentTimeMillis() - t1);

_

8 мс

Теперь побольше

    for (int i = 0; i < 1000; i++) sort(generateRandomArray());

_

6 мс

## Оценка с позиции черный ящик (В основном web)

- JMeter
- Gatling

## Оценка узкого горлышка

- JVisualVM
- Yourkit Java Profiler (Платный, но очень хороший, есть триал)

## Highload

- https://www.youtube.com/channel/UCwHL6WHUarjGfUM_586me8w
- https://github.com/donnemartin/system-design-primer

# Паттерны

- Э. Гамма и др. Паттерны объектно-ориентированного проектирования (2020)
- https://ru.wikipedia.org/wiki/Design_Patterns

# Рефакторинг

- Макконнелл С. "Совершенный код"
- https://refactoring.guru/ru (доступ через прокси/vpn)
