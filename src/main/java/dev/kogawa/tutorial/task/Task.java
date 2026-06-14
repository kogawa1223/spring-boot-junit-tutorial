package dev.kogawa.tutorial.task;

/**
 * Task ドメイン。Java の record（不変・equals/hashCode/toString 自動生成）で表す。
 */
public record Task(Long id, String title, boolean done) {

    /** done を切り替えた新しい Task を返す（record は不変なので copy-on-write）。 */
    public Task toggle() {
        return new Task(id, title, !done);
    }
}
