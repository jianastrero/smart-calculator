package calculator

import java.lang.Exception
import java.math.BigInteger
import java.util.Scanner
import java.util.Stack
import kotlin.math.pow

fun main() {

    val scanner = Scanner(System.`in`)
    val smartCalculator = SmartCalculator()

    while (true) {
        val input = scanner.nextLine()
        try {
            when {
                input.startsWith("/") -> {
                    when (input) {
                        "/exit" -> {
                            println(smartCalculator.exit())
                            return
                        }
                        "/help" -> {
                            println(smartCalculator.help())
                        }
                        else -> {
                            println("Unknown command")
                        }
                    }
                }
                input.contains("=") -> {
                    smartCalculator.addVariable(input)
                }
                input.isNotEmpty() -> {
                    println(smartCalculator.evaluate(input))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
    }
}

// 8 * 3 + 12 * (4 - 2)

class SmartCalculator {
    private val variables = mutableMapOf<String, String>()

    fun exit() = "Bye!"

    fun help() =
        """
            The program calculates the sum of numbers.
            Even number of minuses gives a plus, and the odd number of minuses gives a minus/
        """.trimIndent()

    fun addVariable(parsableAssignment: String) {
        val x = parsableAssignment.replace("\\s+".toRegex(), "")
            .split("=")

        if (!x[0].matches("[A-Za-z]+".toRegex())) {
            throw InvalidIdentifierException()
        } else if (x.size != 2 || !x[1].isValidVariableName() && !x[1].isValidValue()) {
            throw InvalidAssignmentException()
        } else if (x[1].isValidVariableName() && variables[x[1]] == null) {
            throw UnknownVariableException()
        } else {
            variables[x[0]] = x[1]
        }
    }

    private fun postfix(equation: String): MutableList<String> {
        return try {
            val equation = equation.split("\\s+".toRegex())

            val list = mutableListOf<String>()
            val operatorStack = Stack<Char>()

            if (equation.size == 1 && equation[0].isValidValue()) {
                list.add(equation[0])
            } else {
                var index = 0
                while (equation.size > index) {
                    val str = equation[index]

                    if (str[0].isOperator()) {
                        val lastOperator: Char? = operatorStack.lastOrNull()
                        if (lastOperator.getPriority() >= str[0].getPriority()) {
                            list.add("${operatorStack.pop()}")
                        }
                        operatorStack.push(str[0])
                    } else if (str[0] == '(') {
                        var nextEquation = str
                        index++

                        while (equation.size > index) {
                            nextEquation += equation[index]

                            if (nextEquation.count { it == '(' } == nextEquation.count { it == ')' }) {
                                val nextStack = postfix(fixEquation(nextEquation.substring(1, nextEquation.length - 1)))
                                list.addAll(nextStack)
                                if (operatorStack.isNotEmpty()) {
                                    list.add(operatorStack.pop().toString())
                                }
                                break
                            }

                            index++
                        }
                    } else {
                        list.add(str)
                    }

                    index++
                }

                while (operatorStack.isNotEmpty()) {
                    list.add(operatorStack.pop().toString())
                }
            }

            list
        } catch (e: Exception) {
            e.printStackTrace()
            throw  InvalidExpressionException()
        }
    }

    fun evaluate(equation: String): BigInteger {
        try {
            val fixedEquation = fixEquation(equation)
            val postfix = postfix(fixedEquation)
            val stack = Stack<BigInteger>()

            while (postfix.isNotEmpty()) {

                val x = postfix.first()
                postfix.removeAt(0)

                if (x.isValidValue()) {
                    stack.push(x.toBigInteger())
                } else {
                    val b = stack.pop()
                    val a = stack.pop()

                    val y = when (x) {
                        "^" -> {
                            a.pow(b.intValueExact())
                        }
                        "*" -> {
                            a * b
                        }
                        "/" -> {
                            a / b
                        }
                        "+" -> {
                            a + b
                        }
                        "-" -> {
                            a - b
                        }
                        else -> {
                            BigInteger.ZERO
                        }
                    }

                    stack.push(y)
                }
            }

            return stack.pop()
        } catch (e: Exception) {
            e.printStackTrace()
            throw InvalidExpressionException()
        }
    }

    private fun fixEquation(equation: String): String {
        val operators = equation.split("\\s*[^+\\-/*]+\\s*".toRegex())
            .filter { it.isNotEmpty() }
        val operands = equation.split("\\s*[+\\-/*]+\\s*".toRegex())
            .mapIndexed { index, s ->
                val t = s.replace("\\s+", "")
                if (index == 0 && t.isEmpty()) {
                    0
                } else if (t.isNotEmpty()) {

                    val x = t.replace("[()]+".toRegex(), "")
                    val prefix = t.replace("[\\da-zA-Z]+[()]*".toRegex(), "")
                    val suffix = t.replace("[()]*[\\da-zA-Z]+".toRegex(), "")

                    "$prefix${getValue(x)}$suffix"
                } else {
                    throw InvalidExpressionException()
                }
            }
            .toMutableList()

        if (operators.size != operands.size - 1)
            throw InvalidExpressionException()

        var x = "${operands[0]}"

        operators.indices.forEach {
            x += " ${simplifyOperator(operators[it])} ${operands[it + 1]}"
        }

        return x
    }

    private fun Char?.getPriority(): Int {
        this?.let { operator ->
            arrayOf("+-".toCharArray(), "*/".toCharArray(), charArrayOf('^'))
                .forEachIndexed { index, it ->
                    if (operator in it)
                        return index
                }
        }

        return -1
    }

    private fun getValue(variable: String): String {
        var value = variables[variable]

        if (variable.isValidValue()) {
            return variable
        } else if (value != null && !value.isValidValue()) {
            value = getValue(value)
        }

        return value ?: throw UnknownVariableException()
    }

    private fun simplifyOperator(operator: String): String {
        val y = operator.replace("\\s+".toRegex(), "")

        return if (y.length > 1) {
            if (y.all { it == '+' }) {
                "+"
            } else {
                when (y.replace("+", "").count() % 2) {
                    0 -> "+"
                    1 -> "-"
                    else -> throw InvalidExpressionException()
                }
            }
        } else {
            y
        }
    }

    private fun String?.isValidVariableName() = this?.matches("[A-Za-z]+".toRegex()) ?: false

    private fun String?.isValidValue() = this?.matches("\\d+".toRegex()) ?: false

    private fun Char?.isOperator() = this?.let { it in "^*/+-".toCharArray() } ?: false

    class InvalidExpressionException : Exception("Invalid expression")
    class InvalidIdentifierException : Exception("Invalid identifier")
    class InvalidAssignmentException : Exception("Invalid assignment")
    class UnknownVariableException : Exception("Unknown variable")
}