package com.todoapp.implement.repository;

import com.todoapp.domain.Todo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByCompleted(boolean completed);

    List<Todo> findByTitleContaining(String title);
}
