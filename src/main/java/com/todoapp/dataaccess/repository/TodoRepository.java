package com.todoapp.dataaccess.repository;

import com.todoapp.dataaccess.entity.Todo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserId(Long id);

    Optional<Todo> findOwnedTodoByIdAndUserId(Long id, Long userId);
}
