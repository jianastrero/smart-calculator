import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    val x = IntArray(scanner.nextInt()) { scanner.nextInt() }
    val shift = scanner.nextInt()

    println(
        x.indices
            .map {
                val a = (it + x.size - shift) % x.size
                if (a < 0)
                    x.size + a
                else
                    a
            }
            .map { x[it] }
            .joinToString(" ")
    )
}