fun main() {
    val letters = mutableMapOf<Int, String>()

    var i = 0
    while (true) {
        val x = readLine()!!
        letters[i] = x
        i++

        if (x == "z") {
            break
        }
    }

    print(letters[3])
}