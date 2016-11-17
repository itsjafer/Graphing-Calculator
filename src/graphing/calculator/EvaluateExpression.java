/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphing.calculator;

/**
 *
 * @author Jafer Haider
 */
public class EvaluateExpression {

    int pos, c;
    String str;

    public EvaluateExpression(String str) {
        this.pos = -1;
        this.str = str;
    }

    public void eatChar() {
        if (++pos < str.length()) {
            c = str.charAt(pos);
        } else {
            c = -1;
        }
    }

    public void eatSpace() {
        while (Character.isWhitespace(c)) {
            eatChar();
        }
    }

    double parseFactor() {
        double v;
        boolean negate = false;
        eatSpace();
        if (Character.isAlphabetic(c) && c != 'e') {
            char function = (char) c;
            eatChar();
            eatChar();
            eatChar();
            if (function == 's') {
                v = Math.sin(parseFactor());
            }
            else if (function == 'c') {
                v = Math.cos(parseFactor());
            }
            else if (function == 't') {
                v = Math.tan(parseFactor());
            }
            else if (function == 'l') {
                v = Math.log10(parseFactor());
            }
            else v = parse();
        } else if (c == '(') { // brackets
            eatChar();
            v = parseExpression();
            if (c == ')') {
                eatChar();
            }
        } else { // numbers
            if (c == '+' || c == '-') { // unary plus & minus
                negate = c == '-';
                eatChar();
                eatSpace();
            }
            StringBuilder sb = new StringBuilder();
            while ((c >= '0' && c <= '9') || c == '.' || c == 'e') {
                sb.append((char) c);
                eatChar();
            }
            if (sb.length() == 0) {
                throw new RuntimeException("Unexpected: " + (char) c);
            }
            v = Double.parseDouble(sb.toString());
        }
        eatSpace();
        if (c == '^') { // exponentiation
            eatChar();
            v = Math.pow(v, parseFactor());
        }
        if (negate) {
            v = -v; // exponentiation has higher priority than unary minus: -3^2=-9
        }
        return v;
    }

    double parseTerm() {
        double v = parseFactor();
        for (;;) {
            eatSpace();
            if (c == '/') { // division
                eatChar();
                v /= parseFactor();
            } else if (c == '*' || c == '(') { // multiplication
                if (c == '*') {
                    eatChar();
                }
                v *= parseFactor();
            } else {
                return v;
            }
        }
    }

    double parseExpression() {
        double v = parseTerm();
        for (;;) {
            eatSpace();
            if (c == '+') { // addition
                eatChar();
                v += parseTerm();
            } else if (c == '-') { // subtraction
                eatChar();
                v -= parseTerm();
            } else {
                return v;
            }
        }
    }

    double parse() {
        eatChar();
        double v = parseExpression();
        if (c != -1) {
            throw new RuntimeException("Unexpected: " + (char) c);
        }
        return v;
    }
}
