import java.math.BigInteger

fun main() {
    val x = Array(2) { BigInteger(readLine()!!) }
    x.forEach { print("${ (it * 100.toBigInteger()).divide(x.reduce { a, b -> a + b }) }% ") }
}
