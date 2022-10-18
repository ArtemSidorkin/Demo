# Generics

## Введение

До Generics:

    List accounts = new ArrayList();
    
    accounts.add(10);
    accounts.add(20);
    accounts.add(30);
    
    int sum = 0;
    
    for (int i = 0; i < accounts.size(); i++) sum += (Integer) accounts.get(0);
    
    System.out.println(sum);

После:

    List<Integer> accounts = new ArrayList<>();

    accounts.add(10);
    accounts.add(20);
    accounts.add(30);

    int sum = 0;

    for (int i = 0; i < accounts.size(); i++) sum += accounts.get(0);

    System.out.println(sum);

## Ковариантность, контравариантность и инвариантность

Дженерики ковариантны

    List<Number> numbers;
    
    // Так делать нельзя - ошибка компиляции
    numbers = new ArrayList<Integer>();

    List<Integer> integers;

    // И так тоже
    integers = new ArrayList<Number>();

Но могут быть ковариантны

    List<? extends Number> numbers;

    // Все в порядке
    numbers = new ArrayList<Integer>();

Или контрвариантны

    List<? super Integer> integers;

    // Тоже все хорошо
    integers = new ArrayList<Number>();

## PECS (Producer Extends Consumer Super)

Если wildcard(?) использует extends - он может писать (producer), если использует super - читать (consumer):

    public class A {}
    public class B extends A {}
    public class C extends B {}

	private static void producer(List<? extends B> list)
	{
		list.add(new A()); //Ошибка компиляции
		list.add(new B()); //Ошибка компиляции
		list.add(new C()); //Ошибка компиляции

		A a = list.get(0);
		B b = list.get(0);
		C c = list.get(0); //Ошибка компиляции
	}

	private static void consumer(List<? super B> list)
	{
		list.add(new A()); //Ошибка компиляции
		list.add(new B());
		list.add(new C());

		A a = list.get(0); //Ошибка компиляции
		B b = list.get(0); //Ошибка компиляции
		C c = list.get(0); //Ошибка компиляции
	}

Зачем? Ну, иногда, в специфических случаях, это полезно:

	public static void main(String[] args)
	{
		List<B> source = new ArrayList<>(Arrays.asList(new B(), new B(), new B()));
		List<B> target = new ArrayList<>(Arrays.asList(null, null, null));

		copy(source, target);

		System.out.println(target);
	}

	private static void copy(List<? extends B> source, List<? super B> target)
	{
		for (int i = 0; i < source.size() && i < target.size(); i++) target.set(i, source.get(i));
	}

_

    [com.example.demo.B@15db9742, com.example.demo.B@6d06d69c, com.example.demo.B@7852e922]

Однако, эту же задачу можно решить типизировав параметры

	private static <T extends B> void copy(List<T> source, List<T> target)
	{
		for (int i = 0; i < source.size() && i < target.size(); i++) target.set(i, source.get(i));
	}

Но, преимущество wild card - в более точном выражении намерений и иногда упрощении сигнатуры метода (легче читать)

## Generic Class

    public class Container<T>
    {
        private T value;
    
        public Container(T value)
        {
            this.value = value;
        }
    
        public Container() {}
    
        public T getValue()
        {
            return value;
        }
    
        public void setValue(T value)
        {
            this.value = value;
        }
    }

## Стирание

Важно понимать, что дженерики видны только во время компиляции (защита кода). Все информации о типах не доступна во время выполнения.
Однако, с помощью рефлексии можно получить информацию о дженериках в полях и параметрах методов.

## Рекомендуемая литература

- Thinking in Java Fourth Edition (Bruce Eckel)
- https://habr.com/ru/company/sberbank/blog/416413/