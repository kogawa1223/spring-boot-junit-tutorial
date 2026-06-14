package dev.kogawa.tutorial.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 トピック: テストライフサイクルと @Nested。
 * @BeforeEach/@AfterEach は各テストごと、@BeforeAll/@AfterAll はクラスで1回（static）。
 * @Nested で文脈ごとにテストをグループ化できる。
 */
@DisplayName("JUnit5: ライフサイクルとネスト")
class LifecycleNestedTest {

    private static final List<String> events = new ArrayList<>();
    private List<Integer> list;

    @BeforeAll
    static void beforeAll() {
        events.add("beforeAll");
    }

    @AfterAll
    static void afterAll() {
        // クラス内の全テスト後に1回。各 before/afterEach がテストごとに走ったことを確認。
        assertTrue(events.contains("beforeAll"));
        assertTrue(events.stream().filter(e -> e.equals("beforeEach")).count() >= 2);
    }

    @BeforeEach
    void beforeEach() {
        events.add("beforeEach");
        list = new ArrayList<>();  // テストごとに新しい状態を用意（テスト分離）
    }

    @AfterEach
    void afterEach() {
        events.add("afterEach");
    }

    @Test
    @DisplayName("最初のテストは空のリストから始まる")
    void startsEmpty() {
        assertTrue(list.isEmpty());
        list.add(1);
    }

    @Test
    @DisplayName("別テストでは前テストの変更が残らない（分離されている）")
    void isolatedFromOtherTests() {
        assertTrue(list.isEmpty());  // 前テストで add した 1 は見えない
        list.add(2);
        assertEquals(1, list.size());
    }

    @Nested
    @DisplayName("空のスタックという文脈で")
    class WhenEmpty {

        @Test
        @DisplayName("サイズは 0")
        void sizeIsZero() {
            assertEquals(0, list.size());  // 外側の @BeforeEach も継承される
        }
    }
}
