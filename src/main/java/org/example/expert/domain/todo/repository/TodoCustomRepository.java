package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoCustomRepository {

	Page<Todo> searchTodos(
		String weather,
		LocalDateTime startDate,
		LocalDateTime endDate,
		Pageable pageable
	);

	Page<TodoSearchResponse> getSearchTodos(
		String title,
		LocalDateTime createdAt,
		String nickname,
		Pageable pageable
	);
}
