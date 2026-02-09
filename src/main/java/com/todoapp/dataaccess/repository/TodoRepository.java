package com.todoapp.dataaccess.repository;

import com.todoapp.dataaccess.entity.Todo;
import com.todoapp.dataaccess.entity.User;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserId(Long id);

    Optional<Todo> findByIdAndUserId(Long id, Long userId);

    List<Todo> findByCompleted(boolean completed);

    List<Todo> findByTitleContaining(String title);
}
