package com.ufu.domain.itemsubmission.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class ItemSubmissionRequest {
    @NotBlank(message = "아이템 이름은 필수입니다")
    @Size(max = 15, message = "아이템 이름은 15자 이하로 입력해주세요")
    private String name;

    @NotBlank(message = "아이템 설명은 필수입니다")
    @Size(max = 200, message = "아이템 설명은 200자 이하로 입력해주세요")
    private String description;

    @NotNull(message = "아이템 이미지는 필수입니다")
    private MultipartFile image;
}
