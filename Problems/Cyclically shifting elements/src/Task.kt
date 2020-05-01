import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    val x = IntArray(scanner.nextInt()) { scanner.nextInt() }.toMutableList()
    x.add(0, x.removeAt(x.size - 1))

    println(x.joinToString(" "))
}