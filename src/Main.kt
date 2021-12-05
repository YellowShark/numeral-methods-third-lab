fun main() {
    val data = listOf(10f, 100f, 1000f)
    for (i in 0 until 9)
    {
        print("Номер теста ${i + 1}\tРазмерность ${data[i % 3]}\tДиапазон значений [-${data[i % 3]}; ${data[i % 3]}]\t")
        Tester().test(20, data[i / 3], data[i % 3].toInt()).also {
            println("Средняя относительная погрешность ${it.first}\tСреднее значение оценки точности ${it.second}")
        }
    }
}