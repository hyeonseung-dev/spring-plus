package org.example.expert.domain.manager.service;

import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

	private final LogRepository logRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveManagerLog(
		Long requesterId,
		String requesterNickname,
		Long todoId,
		String managerNickname,
		boolean success,
		String message
	) {
		Log log = new Log(
			requesterId,
			requesterNickname,
			todoId,
			managerNickname,
			success,
			message
		);

		logRepository.save(log);
	}
}