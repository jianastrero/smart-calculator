package calculator

import java.lang.Exception
import java.util.Scanner

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
        }catch (e: Exception) {
            println(e.message)
        }
    }
}

// 3 8 4 3 + 2 * 1 + * + 6 2 1 + / -
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

    fun evaluate(equation: String): Int {
        val operators = equation.split("\\s*[^+\\-/*]+\\s*".toRegex())
            .filter { it.isNotEmpty() }
        val operands = equation.split("\\s*[+\\-/*]+\\s*".toRegex())
            .mapIndexed { index, s ->
                if (index == 0 && s.isEmpty()) {
                    0
                } else if (s.isNotEmpty()) {
                    getValue(s).toInt()
                } else {
                    throw InvalidExpressionException()
                }
            }
            .toMutableList()

        if (operators.size != operands.size - 1)
            throw InvalidExpressionException()

        operators.forEachIndexed { index, s ->
            when (simplifyOperator(s)) {
                "+" -> operands[index + 1] = operands[index] + operands[index + 1]
                "-" -> operands[index + 1] = operands[index] - operands[index + 1]
                "*" -> operands[index + 1] = operands[index] * operands[index + 1]
                "/" -> operands[index + 1] = operands[index] / operands[index + 1]
            }
        }

        return operands[operands.size - 1]
    }

    private fun getValue(variable: String): String {
        var value = variables[variable]

        if (value != null && !value.isValidValue()) {
            value = getValue(value)
        } else if (variable.isValidValue()) {
            return variable
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

    class InvalidExpressionException : Exception("Invalid expression")
    class InvalidIdentifierException : Exception("Invalid identifier")
    class InvalidAssignmentException : Exception("Invalid assignment")
    class UnknownVariableException : Exception("Unknown variable")
}