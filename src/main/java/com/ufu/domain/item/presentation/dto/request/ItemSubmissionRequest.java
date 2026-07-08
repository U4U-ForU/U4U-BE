package com.ufu.domain.item.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@Schema(description = "아이템 제출 요청")
public class ItemSubmissionRequest {
    @NotBlank(message = "아이템 이름은 필수입니다")
    @Size(max = 30, message = "아이템 이름은 30자 이하로 입력해주세요")
    @Schema(description = "아이템 이름", example = "우주 고양이")
    private String name;

    @NotBlank(message = "아이템 설명은 필수입니다")
    @Size(max = 1000, message = "아이템 설명은 1000자 이하로 입력해주세요")
    @Schema(description = "아이템 설명", example = "별빛을 모으는 고양이 아이템입니다.")
    private String description;

    @NotNull(message = "아이템 이미지는 필수입니다")
    @Schema(description = "아이템 이미지 파일", type = "string", format = "binary")
    private MultipartFile image;
}
