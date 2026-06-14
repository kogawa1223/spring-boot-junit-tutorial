package dev.kogawa.tutorial.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Spring Boot トピック: 統合テスト (@SpringBootTest)。
 * アプリの全コンテキスト（実 Service 含む）をロードし、本物の配線で検証する。
 * スライステストとの違い: モックを使わず、実際のフローを端から端まで通す。
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Spring: Task API 統合テスト")
class TaskApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskService service;

    @BeforeEach
    void reset() {
        service.clear();  // テスト間の状態を分離
    }

    @Test
    @DisplayName("作成 → 取得 → トグル → 削除 の一連フローが通る")
    void fullLifecycle() throws Exception {
        // 作成
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"learn spring\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.done").value(false));

        // 取得
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("learn spring"));

        // トグル（done が true になる）
        mockMvc.perform(patch("/api/tasks/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));

        // 削除
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        // 削除後は 404
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("一覧は作成順に並ぶ")
    void listIsOrdered() throws Exception {
        service.create("first");
        service.create("second");

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("first"))
                .andExpect(jsonPath("$[1].title").value("second"));
    }
}
