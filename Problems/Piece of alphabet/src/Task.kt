fun main() {
    val x = readLine()!!

    var ok = true
    x.toCharArray().forEachIndexed { index, c ->
        ok = index == c.toInt() - x[0].toInt()
    }

    println(ok)
}