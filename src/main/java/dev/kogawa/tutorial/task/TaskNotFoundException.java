package dev.kogawa.tutorial.task;

/**
 * 指定 id の Task が無いときに送出する。GlobalExceptionHandler が 404 に変換する。
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task が見つからない: id=" + id);
    }
}
