const display = document.getElementById("display");
let justCalculated = false;

function clearDisplay() {
    display.textContent = "0";
    justCalculated = false;
}

function appendcharacter(character) {
    if (!"0123456789.+-*/".includes(character)) {
        return;
    }

    let value = display.textContent;
    if (value === "Error") {
        value = "0";
        justCalculated = false;
    } else if (justCalculated) {
        if (!"+-*/".includes(character)) {
            value = "0";
        }
        justCalculated = false;
    }

    if ("+-*/".includes(character)) {
        if (/[+\-*/]$/.test(value)) {
            const hasUnaryMinus = value.endsWith("-")
                && value.length > 1
                && /[+\-*/]$/.test(value.slice(0, -1));

            if (character === "-" && !hasUnaryMinus) {
                display.textContent = value + character;
                return;
            }

            if (hasUnaryMinus) {
                value = value.slice(0, -1);
            }
            value = value.slice(0, -1);
        } else if (value === "0" && character !== "-") {
            return;
        }

        if (value.endsWith(".")) {
            value += "0";
        }
        display.textContent = value + character;
        return;
    }

    if (character === ".") {
        const lastOperator = Math.max(
            value.lastIndexOf("+"),
            value.lastIndexOf("-"),
            value.lastIndexOf("*"),
            value.lastIndexOf("/")
        );
        const currentNumber = value.slice(lastOperator + 1);
        if (currentNumber.includes(".")) {
            return;
        }
        display.textContent = value + (/[+\-*/]$/.test(value) ? "0." : ".");
        return;
    }

    if (value === "0") {
        display.textContent = character;
    } else {
        display.textContent = value + character;
    }
}

function deletelast() {
    if (display.textContent === "Error" || justCalculated) {
        clearDisplay();
        return;
    }

    const value = display.textContent.slice(0, -1);
    display.textContent = value && value !== "-" ? value : "0";
}

function calculatorResult() {
    try {
        const result = evaluateExpression(display.textContent);
        if (!Number.isFinite(result)) {
            throw new Error("The result is not a finite number.");
        }

        display.textContent = String(result);
        justCalculated = true;
    } catch (error) {
        display.textContent = "Error";
        justCalculated = false;
    }
}

function evaluateExpression(expression) {
    const tokens = expression.match(/(?:\d+(?:\.\d*)?|\.\d+)(?:[eE][+-]?\d+)?|[+\-*/]/g);
    if (!tokens || tokens.join("") !== expression || /[+\-*/]$/.test(expression)) {
        throw new Error("Invalid expression.");
    }

    let position = 0;

    function parseFactor() {
        if (tokens[position] === "+" || tokens[position] === "-") {
            const sign = tokens[position] === "-" ? -1 : 1;
            position++;
            return sign * parseFactor();
        }

        const token = tokens[position++];
        if (!token || !/^(?:\d+(?:\.\d*)?|\.\d+)(?:[eE][+-]?\d+)?$/.test(token)) {
            throw new Error("Expected a number.");
        }
        return Number(token);
    }

    function parseTerm() {
        let value = parseFactor();
        while (tokens[position] === "*" || tokens[position] === "/") {
            const operator = tokens[position++];
            const right = parseFactor();
            value = operator === "*" ? value * right : value / right;
        }
        return value;
    }

    function parseExpression() {
        let value = parseTerm();
        while (tokens[position] === "+" || tokens[position] === "-") {
            const operator = tokens[position++];
            const right = parseTerm();
            value = operator === "+" ? value + right : value - right;
        }
        return value;
    }

    const result = parseExpression();
    if (position !== tokens.length) {
        throw new Error("Invalid expression.");
    }
    return result;
}