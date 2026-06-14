package dev.kogawa.tutorial.junit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.kogawa.tutorial.util.Calculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 トピック: Assertions。
 * org.junit.jupiter.api.Assertions の各メソッドと、例外の検証方法を学ぶ。
 */
@DisplayName("JUnit5: アサーション")
class AssertionsTest {

    private final Calculator calc = new Calculator();

    @Test
    @DisplayName("基本のアサーション (equals / true / false)")
    void basicAssertions() {
        assertEquals(5, calc.add(2, 3), "2 + 3 は 5");
        assertTrue(calc.isPrime(7));
        assertFalse(calc.isPrime(8));
    }

    @Test
    @DisplayName("assertAll は全アサーションをまとめて評価する（最初の失敗で止まらない）")
    void groupedAssertions() {
        assertAll("calculator",
                () -> assertEquals(1, calc.subtract(3, 2)),
                () -> assertEquals(6, calc.add(4, 2)),
                () -> assertEquals(4, calc.divide(8, 2)));
    }

    @Test
    @DisplayName("assertThrows で例外の型とメッセージを検証する")
    void exceptionTesting() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> calc.divide(1, 0));
        assertEquals("0 では割れない", ex.getMessage());
    }
}
