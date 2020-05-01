fun main() {

    val a = when (val letter = readLine()!!.toLowerCase()[0]) {
        'a', 'e', 'i', 'o', 'u' -> {
            letter.toInt() - 'a'.toInt() + 1
        }
        else -> 0
    }
    
    println(a)
}
