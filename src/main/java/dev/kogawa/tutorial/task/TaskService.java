package dev.kogawa.tutorial.task;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/**
 * インメモリの Task ストア。DB を使わず学習に集中するため Map で保持する。
 * @Service で Spring の DI コンテナに登録される。
 */
@Service
public class TaskService {

    private final ConcurrentHashMap<Long, Task> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public List<Task> findAll() {
        return store.values().stream()
                .sorted((a, b) -> Long.compare(a.id(), b.id()))
                .toList();
    }

    public Task findById(Long id) {
        return Optional.ofNullable(store.get(id))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(String title) {
        long id = sequence.incrementAndGet();
        Task task = new Task(id, title, false);
        store.put(id, task);
        return task;
    }

    public Task toggle(Long id) {
        Task toggled = findById(id).toggle();
        store.put(id, toggled);
        return toggled;
    }

    public void delete(Long id) {
        if (store.remove(id) == null) {
            throw new TaskNotFoundException(id);
        }
    }

    /** テストの分離のために使う（各テスト前に状態をリセット）。 */
    public void clear() {
        store.clear();
        sequence.set(0);
    }
}
