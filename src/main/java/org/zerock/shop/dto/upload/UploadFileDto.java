package org.zerock.shop.dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFileDto {
    // MultipartFile로 지정해 주면 간단한 파일 업로드 처리는 가능하지만
    // Swagger UI와 같은 프레임워크로 테스트하기 불편하기 때문에 별도의 Dto 선언
    // 업로드 처리를 위한 Dto

    private List<MultipartFile> files;

}
