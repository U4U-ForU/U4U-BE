package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionForbiddenException;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotFoundException;
import com.ufu.domain.itemsubmission.repository.ItemSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 아이템 제출 유스케이스들이 공유하는 조회/검증 헬퍼다.
 *
 * <p><b>이 클래스에는 절대 {@code @Transactional}을 붙이지 않는다.</b>
 * 호출자의 트랜잭션 안에서 실행되어야 반환한 엔티티가 영속 상태로 유지되고,
 * 호출자의 변경 감지(dirty checking)가 동작한다.
 * 자체 트랜잭션을 열면 반환값이 준영속 상태가 되어 상태 변경이 반영되지 않는다.
 * 같은 이유로 Controller에 직접 주입하지 않는다.
 * ({@code TradeTransactionService}, {@code CauldronRecipeSupport}와 동일한 규약이다.)
 */
@Service
@RequiredArgsConstructor
public class ItemSubmissionSupport {
    private final ItemSubmissionRepository itemSubmissionRepository;

    public ItemSubmission findSubmission(String submissionId) {
        return itemSubmissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ItemSubmissionNotFoundException.EXCEPTION);
    }

    public void validateOwner(ItemSubmission itemSubmission, Long submitterId) {
        if (!itemSubmission.isSubmittedBy(submitterId)) {
            throw ItemSubmissionForbiddenException.EXCEPTION;
        }
    }
}
