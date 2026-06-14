package dev.kogawa.tutorial.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.kogawa.tutorial.util.Calculator;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * JUnit 5 トピック: パラメータ化テスト。
 * 1つのテストメソッドを複数の入力で繰り返す。@ValueSource / @CsvSource / @MethodSource。
 */
@DisplayName("JUnit5: パラメータ化テスト")
class ParameterizedTests {

    private final Calculator calc = new Calculator();

    @ParameterizedTest(name = "{0} は素数")
    @ValueSource(ints = {2, 3, 5, 7, 11, 13})
    void primes(int n) {
        assertTrue(calc.isPrime(n));
    }

    @ParameterizedTest(name = "{0} は素数ではない")
    @ValueSource(ints = {1, 4, 6, 8, 9})
    void notPrimes(int n) {
        assertTrue(!calc.isPrime(n));
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "1, 2, 3",
            "5, 5, 10",
            "-1, 1, 0",
    })
    void addWithCsv(int a, int b, int expected) {
        assertEquals(expected, calc.add(a, b));
    }

    @ParameterizedTest
    @MethodSource("divisionCases")
    void divideWithMethodSource(int dividend, int divisor, int expected) {
        assertEquals(expected, calc.divide(dividend, divisor));
    }

    // @MethodSource が参照する static ファクトリ（複雑な引数を組み立てられる）
    static Stream<Arguments> divisionCases() {
        return Stream.of(
                Arguments.of(10, 2, 5),
                Arguments.of(9, 3, 3),
                Arguments.of(7, 2, 3));  // 整数除算なので切り捨て
    }
}
