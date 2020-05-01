fun main() {
    println(readLine()?.map { "$it$it" }?.joinToString(""))
}