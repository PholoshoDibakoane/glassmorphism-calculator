package com.pholosho.glasscalculator

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorEngineTest {
    @Test
    fun respectsOperatorPrecedence() {
        val result = CalculatorEngine.calculate(CalculatorState("8+5*2"))
        assertEquals("18", result.expression)
    }

    @Test
    fun supportsNegativeOperands() {
        val result = CalculatorEngine.calculate(CalculatorState("1+-2"))
        assertEquals("-1", result.expression)
    }

    @Test
    fun handlesDivisionByZero() {
        val result = CalculatorEngine.calculate(CalculatorState("1/0"))
        assertEquals("Error", result.expression)
    }

    @Test
    fun continuesFromCalculatedResult() {
        val first = CalculatorEngine.calculate(CalculatorState("8+5"))
        val second = CalculatorEngine.append(first, '*')
        val third = CalculatorEngine.calculate(CalculatorEngine.append(second, '2'))
        assertEquals("26", third.expression)
    }
}
