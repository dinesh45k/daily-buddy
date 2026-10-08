package com.lifeassistant.task;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDoneFalseOrderByDueDateAsc();
}
