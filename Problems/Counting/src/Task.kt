import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    val haystack = IntArray(scanner.nextInt()) {
        scanner.nextInt()
    }

    val needle = scanner.nextInt()

    println(haystack.count { it == needle })
}