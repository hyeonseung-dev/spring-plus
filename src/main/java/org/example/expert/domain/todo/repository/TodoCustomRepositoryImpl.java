package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.todo.entity.QTodo.*;

import java.time.LocalDateTime;
import java.util.List;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

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
}
