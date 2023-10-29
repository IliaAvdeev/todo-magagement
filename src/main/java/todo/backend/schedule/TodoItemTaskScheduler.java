package todo.backend.schedule;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import todo.backend.repository.TodoItemRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TodoItemTaskScheduler {
    private final TodoItemRepository todoItemRepository;

    @Transactional
    @Scheduled(fixedRate = 2000)
    public void findAndMarkAllExpiredTodoItems() {
        int expiredTodoItems = todoItemRepository.markTodoItemsThatHaveExpired(LocalDateTime.now());
        if (expiredTodoItems > 0) {
            log.info("'{}' expired todo items were found", expiredTodoItems);
        }
    }
}