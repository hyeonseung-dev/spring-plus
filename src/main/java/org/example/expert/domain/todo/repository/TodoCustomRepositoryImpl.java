package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.todo.entity.QTodo.*;

import java.time.LocalDateTime;
import java.util.List;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.comment.entity.QComment.comment;

public class TodoCustomRepositoryImpl implements TodoCustomRepository{

	private final JPAQueryFactory jpaQueryFactory;

	public TodoCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public Page<Todo> searchTodos(
		String weather,
		LocalDateTime startDate,
		LocalDateTime endDate,
		Pageable pageable
	) {
		List<Todo> contents = jpaQueryFactory
			.selectFrom(todo)
			.where(
				weatherEq(weather),
				modifiedAtGoe(startDate),
				modifiedAtLoe(endDate)
			)
			.orderBy(todo.modifiedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(todo.count())
			.from(todo)
			.where(
				weatherEq(weather),
				modifiedAtGoe(startDate),
				modifiedAtLoe(endDate)
			)
			.fetchOne();

		return new PageImpl<>(contents, pageable, total == null ? 0 : total);
	}

	private BooleanExpression weatherEq(String weather) {

		if (weather == null) {
			return null;
		}

		return todo.weather.eq(weather);
	}

	private BooleanExpression modifiedAtGoe(
		LocalDateTime startDate
	) {

		if (startDate == null) {
			return null;
		}

		return todo.modifiedAt.goe(startDate);
	}

	private BooleanExpression modifiedAtLoe(
		LocalDateTime endDate
	) {

		if (endDate == null) {
			return null;
		}

		return todo.modifiedAt.loe(endDate);
	}

	@Override
	public Page<TodoSearchResponse> getSearchTodos(String title, LocalDateTime creatDate, String nickname, Pageable pageable) {
		List<TodoSearchResponse> contents = jpaQueryFactory
			.select(Projections.constructor(
				TodoSearchResponse.class,
				todo.title,
				manager.countDistinct(),
				comment.countDistinct()
			))
			.from(todo)
			.leftJoin(todo.managers, manager)
			.leftJoin(todo.comments, comment)
			.where(
				titleContains(title),
				createdAtEq(creatDate),
				nicknameContains(nickname)
			)
			.groupBy(todo.id)
			.orderBy(todo.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(todo.count())
			.from(todo)
			.where(
				titleContains(title),
				createdAtEq(creatDate),
				nicknameContains(nickname)
			)
			.fetchOne();

		return new PageImpl<>(contents, pageable, total == null ? 0 : total);
	}

	private BooleanExpression titleContains(String title) {
		return StringUtils.hasText(title) ? todo.title.containsIgnoreCase(title) : null;
	}

	private BooleanExpression nicknameContains(String nickname) {
		return StringUtils.hasText(nickname) ? todo.user.nickname.containsIgnoreCase(nickname) : null;
	}

	private BooleanExpression createdAtEq(LocalDateTime createdAt) {
		return createdAt != null ? todo.createdAt.eq(createdAt) : null;
	}
}
