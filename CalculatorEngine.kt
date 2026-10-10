package com.pholosho.glasscalculator

import java.math.BigDecimal
import java.math.RoundingMode

data class CalculatorState(
    val expression: String = "0",
    val justCalculated: Boolean = false
)

object CalculatorEngine {
    fun clear() = CalculatorState()

    fun append(state: CalculatorState, character: Char): CalculatorState {
        if (character !in "0123456789.+-*/") return state

        var expression = state.expression
        var justCalculated = state.justCalculated
        if (expression == "Error") {
            expression = "0"
            justCalculated = false
        } else if (justCalculated) {
            if (character !in "+-*/") expression = "0"
            justCalculated = false
        }

        if (character in "+-*/") {
            if (expression.lastOrNull()?.let { it in "+-*/"} == true) {
                val hasUnaryMinus = expression.endsWith("-") &&
                    expression.length > 1 &&
                    expression.dropLast(1).lastOrNull()?.let { it in "+-*/"} == true

                if (character == '-' && !hasUnaryMinus) {
                    return CalculatorState(expression + character, justCalculated)
                }

                if (hasUnaryMinus) expression = expression.dropLast(1)
                expression = expression.dropLast(1)
            } else if (expression == "0" && character != '-') {
                return CalculatorState(expression, justCalculated)
            }

            if (expression.endsWith('.')) expression += "0"
            return CalculatorState(expression + character, justCalculated)
        }

        if (character == '.') {
            val currentNumber = expression.substringAfterLastAny("+", "-", "*", "/")
            if (currentNumber.contains('.')) return CalculatorState(expression, justCalculated)
            val suffix = if (expression.lastOrNull()?.let { it in "+-*/"} == true) "0." else "."
            return CalculatorState(expression + suffix, justCalculated)
        }

        expression = if (expression == "0") character.toString() else expression + character
        return CalculatorState(expression, justCalculated)
    }

    fun delete(state: CalculatorState): CalculatorState {
        if (state.expression == "Error" || state.justCalculated) return clear()
        val expression = state.expression.dropLast(1)
        return CalculatorState(if (expression.isEmpty() || expression == "-") "0" else expression)
    }

    fun calculate(state: CalculatorState): CalculatorState = try {
        val value = Parser(state.expression).parse()
        if (!value.isFinite()) {
            CalculatorState("Error")
        } else {
            val rounded = BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
            CalculatorState(rounded.toPlainString(), justCalculated = true)
        }
    } catch (_: IllegalArgumentException) {
        CalculatorState("Error")
    } catch (_: ArithmeticException) {
        CalculatorState("Error")
    }

    private fun String.substringAfterLastAny(vararg delimiters: String): String {
        val index = delimiters.maxOf { lastIndexOf(it) }
        return substring(index + 1)
    }

    private class Parser(private val expression: String) {
        private val tokens = Regex("""(?:\d+(?:\.\d*)?|\.\d+)(?:[eE][+-]?\d+)?|[+\-*/]""")
            .findAll(expression)
            .map { it.value }
            .toList()
        private var position = 0

        fun parse(): Double {
            require(tokens.isNotEmpty() && tokens.joinToString("") == expression)
            require(tokens.last() !in listOf("+", "-", "*", "/"))
            val result = parseExpression()
            require(position == tokens.size)
            return result
        }

        private fun parseExpression(): Double {
            var value = parseTerm()
            while (peek() == "+" || peek() == "-") {
                val operator = tokens[position++]
                val right = parseTerm()
                value = if (operator == "+") value + right else value - right
            }
            return value
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (peek() == "*" || peek() == "/") {
                val operator = tokens[position++]
                val right = parseFactor()
                value = if (operator == "*") value * right else value / right
            }
            return value
        }

        private fun parseFactor(): Double {
            if (peek() == "+" || peek() == "-") {
                val sign = if (tokens[position++] == "-") -1.0 else 1.0
                return sign * parseFactor()
            }
            val token = tokens.getOrNull(position++) ?: throw IllegalArgumentException("Expected a number")
            val value = token.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number")
            return value
        }

        private fun peek(): String? = tokens.getOrNull(position)
    }
}
