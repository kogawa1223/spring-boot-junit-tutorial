package dev.kogawa.tutorial.task;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.kogawa.tutorial.web.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Spring Boot トピック: スライステスト (@WebMvcTest)。
 * Web 層（Controller + JSON 変換 + 例外ハンドラ）だけをロードし、
 * Service は @MockitoBean でモックに差し替える。高速で対象が絞れる。
 */
@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Spring: TaskController スライステスト")
class TaskControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TaskService service;

    @Test
    @DisplayName("GET /api/tasks/{id} は Task を JSON で返す")
    void getReturnsTask() throws Exception {
        given(service.findById(1L)).willReturn(new Task(1L, "buy milk", false));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("buy milk"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    @DisplayName("存在しない id は 404 (ProblemDetail)")
    void missingReturns404() throws Exception {
        given(service.findById(99L)).willThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Task が見つからない: id=99"));
    }

    @Test
    @DisplayName("POST で title が空なら 400 (バリデーション)")
    void blankTitleReturns400() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("title は必須"));
    }

    @Test
    @DisplayName("POST で正しい title なら 201 Created")
    void validPostReturns201() throws Exception {
        given(service.create("write tests")).willReturn(new Task(1L, "write tests", false));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"write tests\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("write tests"));

        verify(service).create("write tests");  // 副作用（service 呼び出し）を検証
    }
}
