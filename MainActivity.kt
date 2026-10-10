package com.pholosho.glasscalculator

import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GlassWhite = Color(0xFFFFFFFF)
private val Orange = Color(0xFFFF9F0A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            MaterialTheme {
                GlassCalculator()
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun GlassCalculator() {
    var state by remember { mutableStateOf(CalculatorState()) }
    val buttonHeight = 60.dp
    val gap = 12.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val compact = maxHeight < 600.dp
        Column(
            modifier = Modifier
                .widthIn(max = 368.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = if (compact) 12.dp else 24.dp)
                .shadow(24.dp, RoundedCornerShape(30.dp))
                .background(GlassWhite.copy(alpha = 0.08f), RoundedCornerShape(30.dp))
                .border(1.dp, GlassWhite.copy(alpha = 0.10f), RoundedCornerShape(30.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .border(1.dp, GlassWhite.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 15.dp, vertical = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = state.expression,
                    color = GlassWhite,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    softWrap = false
                )
            }
            Spacer(Modifier.height(20.dp))

            KeyRow(gap) {
                CalculatorKey("AC", KeyKind.Utility, Modifier.weight(1f)) { state = CalculatorEngine.clear() }
                CalculatorKey("÷", KeyKind.Operator, Modifier.weight(1f)) { state = CalculatorEngine.append(state, '/') }
                CalculatorKey("×", KeyKind.Operator, Modifier.weight(1f)) { state = CalculatorEngine.append(state, '*') }
                CalculatorKey("Del", KeyKind.Utility, Modifier.weight(1f)) { state = CalculatorEngine.delete(state) }
            }
            Spacer(Modifier.height(gap))
            KeyRow(gap) {
                NumberKey("7") { state = CalculatorEngine.append(state, '7') }
                NumberKey("8") { state = CalculatorEngine.append(state, '8') }
                NumberKey("9") { state = CalculatorEngine.append(state, '9') }
                CalculatorKey("-", KeyKind.Operator, Modifier.weight(1f)) { state = CalculatorEngine.append(state, '-') }
            }
            Spacer(Modifier.height(gap))
            KeyRow(gap) {
                NumberKey("4") { state = CalculatorEngine.append(state, '4') }
                NumberKey("5") { state = CalculatorEngine.append(state, '5') }
                NumberKey("6") { state = CalculatorEngine.append(state, '6') }
                CalculatorKey("+", KeyKind.Operator, Modifier.weight(1f)) { state = CalculatorEngine.append(state, '+') }
            }
            Spacer(Modifier.height(gap))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(3f),
                    verticalArrangement = Arrangement.spacedBy(gap)
                ) {
                    KeyRow(gap) {
                        NumberKey("1") { state = CalculatorEngine.append(state, '1') }
                        NumberKey("2") { state = CalculatorEngine.append(state, '2') }
                        NumberKey("3") { state = CalculatorEngine.append(state, '3') }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                        CalculatorKey(
                            "0",
                            KeyKind.Number,
                            Modifier.weight(2f),
                            height = buttonHeight,
                            round = 30.dp,
                            startAligned = true
                        ) { state = CalculatorEngine.append(state, '0') }
                        CalculatorKey(".", KeyKind.Number, Modifier.weight(1f), height = buttonHeight) {
                            state = CalculatorEngine.append(state, '.')
                        }
                    }
                }
                CalculatorKey(
                    "=",
                    KeyKind.Operator,
                    Modifier.weight(1f),
                    height = buttonHeight * 2 + gap,
                    round = 30.dp
                ) { state = CalculatorEngine.calculate(state) }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun KeyRow(gap: Dp, content: @androidx.compose.runtime.Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        content()
    }
}

@androidx.compose.runtime.Composable
private fun RowScope.NumberKey(label: String, onClick: () -> Unit) {
    CalculatorKey(label, KeyKind.Number, Modifier.weight(1f)) { onClick() }
}

private enum class KeyKind { Number, Operator, Utility }

@androidx.compose.runtime.Composable
private fun CalculatorKey(
    label: String,
    kind: KeyKind,
    modifier: Modifier,
    height: Dp = 60.dp,
    round: Dp = 50.dp,
    startAligned: Boolean = false,
    onClick: () -> Unit
) {
    val background = when (kind) {
        KeyKind.Number -> GlassWhite.copy(alpha = 0.12f)
        KeyKind.Operator -> Orange.copy(alpha = 0.75f)
        KeyKind.Utility -> GlassWhite.copy(alpha = 0.25f)
    }
    val foreground = if (kind == KeyKind.Utility) Color.Black else GlassWhite
    Box(
        modifier = modifier
            .height(height)
            .shadow(8.dp, RoundedCornerShape(round))
            .background(background, RoundedCornerShape(round))
            .border(
                1.dp,
                if (kind == KeyKind.Operator) Color(0x66FFB446) else GlassWhite.copy(alpha = 0.18f),
                RoundedCornerShape(round)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = if (startAligned) 24.dp else 4.dp),
        contentAlignment = if (startAligned) Alignment.CenterStart else Alignment.Center
    ) {
        Text(
            text = label,
            color = foreground,
            fontSize = 22.sp,
            fontWeight = if (kind == KeyKind.Utility) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1
        )
    }
}
