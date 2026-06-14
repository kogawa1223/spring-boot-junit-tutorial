package dev.kogawa.tutorial.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import dev.kogawa.tutorial.util.Calculator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * JUnit 5 トピック: 前提 (Assumptions) と無効化 (Disabled / 条件付き実行)。
 * - assumeTrue: 前提が崩れたらテストを「失敗」ではなく「スキップ」にする
 * - @Disabled: 一時的に無効化（理由を必ず書く）
 * - @EnabledOnOs: 実行環境による条件付き実行
 */
@DisplayName("JUnit5: 前提と条件付き実行")
class AssumptionsDisabledTest {

    private final Calculator calc = new Calculator();

    @Test
    @DisplayName("前提が満たされる場合だけ本体を検証する")
    void runsOnlyWhenAssumptionHolds() {
        boolean ciEnv = System.getenv("CI") != null;
        assumeTrue(!ciEnv, "ローカルでのみ実行する想定（CI ではスキップ）");
        assertEquals(4, calc.add(2, 2));
    }

    @Test
    @DisplayName("macOS / Linux で実行される")
    @EnabledOnOs({OS.MAC, OS.LINUX})
    void runsOnUnixLike() {
        assertEquals(2, calc.divide(4, 2));
    }

    @Test
    @Disabled("未実装機能のプレースホルダ: 平方根は未対応のため一時的に無効化")
    void notYetImplemented() {
        throw new AssertionError("ここは実行されない");
    }
}
