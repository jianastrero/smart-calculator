fun main() {
    val a = readLine()!!.toLowerCase()

    val vowels = "aeiouy"

    val b = a.split("[$vowels]+".toRegex())
        .filter { it.length > 2 }
        .map { (it.length - 1) / 2 }
        .sum()
    val c = a.split("[^$vowels]+".toRegex())
        .filter { it.length > 2 }
        .map { (it.length - 1) / 2 }
        .sum()

    println(b + c)
}