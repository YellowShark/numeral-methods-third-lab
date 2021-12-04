fun main() {
    val data = listOf(10f, 100f, 1000f)
    for (i in 0 until 9)
    {
        print("number ${i + 1}\tsize ${data[i % 3]}\tinterval [-${data[i % 3]}; ${data[i % 3]}]\t")
        Tester().test(20, data[i / 3], data[i % 3].toInt()).also {
            println("eps avg ${it.first}\tavg mark ${it.second}")
        }
    }
}