package dev.kogawa.tutorial.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Task 作成リクエスト。Bean Validation のアノテーションで入力を検証する。
 * Controller 側で @Valid を付けると、違反時に自動で 400 になる。
 */
public record CreateTaskRequest(
        @NotBlank(message = "title は必須")
        @Size(max = 100, message = "title は 100 文字以内")
        String title) {
}
