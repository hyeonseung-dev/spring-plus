package org.example.expert.domain.manager.entity;

import org.example.expert.domain.common.entity.Timestamped;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "log")
public class Log extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long requesterId;

	private String requesterNickname;

	private Long todoId;

	private String managerNickname;

	private boolean success;

	private String message;

	public Log(
		Long requesterId,
		String requesterNickname,
		Long todoId,
		String managerNickname,
		boolean success,
		String message
	) {
		this.requesterId = requesterId;
		this.requesterNickname = requesterNickname;
		this.todoId = todoId;
		this.managerNickname = managerNickname;
		this.success = success;
		this.message = message;
	}
}
