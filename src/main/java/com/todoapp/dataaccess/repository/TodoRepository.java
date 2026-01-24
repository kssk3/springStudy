package com.todoapp.dataaccess.repository;

import com.todoapp.dataaccess.entity.Todo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByCompleted(boolean completed);

    List<Todo> findByTitleContaining(String title);
}
