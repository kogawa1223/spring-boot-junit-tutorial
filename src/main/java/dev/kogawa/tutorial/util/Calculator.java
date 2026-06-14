package dev.kogawa.tutorial.util;

/**
 * Spring に依存しない純粋な Java クラス。
 * JUnit 5 の基本（assertions / parametrized / assertThrows）を学ぶ題材にする。
 */
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    /**
     * 整数の割り算。0 除算は例外（assertThrows のデモ用）。
     */
    public int divide(int dividend, int divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("0 では割れない");
        }
        return dividend / divisor;
    }

    public boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; (long) i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
