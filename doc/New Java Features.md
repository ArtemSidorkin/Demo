# Новые возможности начиная с java 8 до java 17

## Лямбды

Раньше чтобы передать действие как параметр нужно было использовать многословные анонимные классы.

Декларируем интерфейс с нужной сигнатурой обработки данных:

    public interface IntegerHandler
    {
        void apply(int integer);
    }

Декларируем метод где нужно передавать обработчик:

	private static void iterateOverIntegerList(List<Integer> list, IntegerHandler handler)
	{
        for (Integer i : list) handler.apply(i);
	}

Используем:

    iterateOverIntegerList(
        Arrays.asList(1, 2, 3),
        new IntegerHandler()
        {
            @Override
            public void apply(int integer)
            {
                System.out.println(integer);
            }
        }
    );

Теперь, если интерфейс имеет 1 метод, вместо анонимного класса можно использовать лямбды

    iterateOverIntegerList(Arrays.asList(1, 2, 3), integer -> System.out.println(integer));

Так же если сигнатура метода совпадает с требуемым интерфейсом, можно использовать ссылку на метод

    iterateOverIntegerList(Arrays.asList(1, 2, 3), System.out::println);

Многострочные лямбды

    iterateOverIntegerList(
        Arrays.asList(1, 2, 3),
        integer ->
        {
            int square = integer * integer;

            System.out.println(square);
        }
    );

Java 8 вводит несколько интерфейсов для общих методов, чтобы каждый раз не создавать свои, например Consumer:

	private static void iterateOverIntegerList(List<Integer> list, Consumer<Integer> consumer)
	{
		for (Integer i : list) consumer.accept(i);
	}

Код для работы аналогичен

    iterateOverIntegerList(
        Arrays.asList(1, 2, 3),
        integer ->
        {
            int square = integer * integer;

            System.out.println(square);
        }
    );

Большинство можно увидеть рассмотрев параметры обработчиков потоков

## Обработка коллекций с помощью потоков

Задача обойти список целых чисел и напечатать положительные числа до java 8

    for (Integer i : list)
        if (i > 0) System.out.println(i);

Задача обойти список целых чисел и напечатать положительные числа после java 8

    list.stream().filter(i -> i > 0).forEach(System.out::println);

Польза в таком простом пример неочевидна, но демонстрирует разницу в подходах

А что если нужно получить новый список с положительными числами старого?

до java 8

    List<Integer> newList = new ArrayList<>();

    for (Integer i : list)
        if (i > 0) newList.add(i);

после

    list.stream().filter(i -> i > 0).collect(Collectors.toList());

### Некоторые полезные методы

#### map

    Stream.of(1, 2, 3).map(i -> i * i).forEach(System.out::println);

_

    > Task :Main.main()
    1
    4
    9

#### distinct

    Stream.of(1, 1, 2, 2, 3, 3).distinct().forEach(System.out::println);

_

    > Task :Main.main()
    1
    2
    3

#### all match

    System.out.println(Stream.of(1, 2, 3).allMatch(i -> i > 0));

_

    true

#### any match

    System.out.println(Stream.of(-1, 2, 3).anyMatch(i -> i < 0));

_

    true

#### none match

    System.out.println(Stream.of(1, 2, 3).noneMatch(i -> i < 0));

_

    true

#### flat map

		out
			.println(
				asList(asList(1, 2, 3), asList(4, 5, 6), asList(7, 8, 9))
					.stream()
					.flatMap(integers -> integers.stream())
					.collect(Collectors.toList())
			);


#### reduce

    System.out.println(Stream.of(1, 2, 3, 4, 5).reduce((a, b) -> a + b))

_

    Optional[15]

## Optional

Используются вместо null, если метод может принять или вернуть пустое (null) значение, стоит использовать Optional:

	public static void main(String[] args)
	{
		Optional<Integer> value;

		value = Optional.of(123);
		value = Optional.ofNullable(randomIntegerOrNull());
		value = Optional.empty();
	}

	private static Integer randomIntegerOrNull()
	{
		if (Math.random() > 0) return (int)(Math.random() * 100);
		else return null;
	}

Optional обладает некоторыми свойствами потоков, например метод map

    System.out.println(Optional.of(10).map(i -> i * i));
    System.out.println(Optional.<Integer>empty().map(i -> i * i));

_

    Optional[100]
    Optional.empty

Получить значение можно следующим образом

    System.out.println(Optional.of(1).get());
    System.out.println(Optional.of(1).orElseGet(() -> 10));
    System.out.println(Optional.of(1).orElse(10));
    System.out.println(Optional.of(1).orElseThrow(() -> new IllegalArgumentException()));

_

    1
    1
    1
    1

_

    System.out.println(Optional.<Integer>empty().get());

_

    Exception in thread "main" java.util.NoSuchElementException: No value present

_

    System.out.println(Optional.<Integer>empty().orElseGet(() -> 10));
    System.out.println(Optional.<Integer>empty().orElse(10));

_

    10
    10

_

    System.out.println(Optional.<Integer>empty().orElseThrow(() -> new IllegalArgumentException()));

_

    Exception in thread "main" java.lang.IllegalArgumentException

## Методы по-умолчанию

    public interface SomeInterface
    {
        default void doSomething()
        {
            System.out.println("Hello World!!!");
        }
    }

## Try with resources

До try with resources

    BufferedReader reader = new BufferedReader(new StringReader("Hello world!"));

    try
    {
        System.out.println(reader.readLine());
    }
    catch (IOException e)
    {
        e.printStackTrace();
    }
    finally
    {
        try
        {
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

После

    try (BufferedReader reader = new BufferedReader(new StringReader("Hello world!")))
    {
        System.out.println(reader.readLine());
    } catch (IOException e)
    {
        e.printStackTrace();
    }

## Вывод типа переменных

    var a = 10;

## Текстовые блоки

    String s =
        """
            <html>
                <head>
                    <title>Hello Wor</title>
                </head>
                <body>
                    <p>Hello World!</p>
                </body>
            </html>
        """;

## Новый instanceof

    Object o = "String";

    if (o instanceof String s) System.out.println(s.length());

## Records

    public class Note
    {
        private String title;
        private String text;
    
        public Note(String title, String text)
        {
            this.title = title;
            this.text = text;
        }
    
        public Note() {}
    
        public String getTitle()
        {
            return title;
        }
    
        public void setTitle(String title)
        {
            this.title = title;
        }
    
        public String getText()
        {
            return text;
        }
    
        public void setText(String text)
        {
            this.text = text;
        }
    
        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            
            if (o == null || getClass() != o.getClass()) return false;
            
            Note note = (Note) o;
            
            return Objects.equals(title, note.title) && Objects.equals(text, note.text);
        }
    
        @Override
        public int hashCode()
        {
            return Objects.hash(title, text);
        }
    
        @Override
        public String toString()
        {
            return "Note{title='" + title + ", text='" + text +'}';
        }
    }

_

    public record Note(String title, String text) {}

## Полезные ссылки

- https://habr.com/ru/post/593243/
- https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/Stream.html
