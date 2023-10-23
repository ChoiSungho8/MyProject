package org.zerock.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.shop.dto.upload.UploadFileDto;
import org.zerock.shop.dto.upload.UploadResultDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
public class UpDownController {
    // 파일 업로드와 파일을 보여주는 기능을 메소드 처리

    // @Value는 application.properties 파일의 설정 정보를 읽어서 변수의 값으로 사용할 수 있다.
    @Value("${org.zerock.upload.path}") // import 시에 springframework로 시작하는 Value
    private String uploadPath;

    @Operation(summary = "Upload POST", description = "POST 방식으로 파일 등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(UploadFileDto uploadFileDto) {

        log.info(uploadFileDto);

        if(uploadFileDto.getFiles() != null) {

            final List<UploadResultDto> list = new ArrayList<>();

            uploadFileDto.getFiles().forEach(multipartFile -> {

                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);

                String uuid = UUID.randomUUID().toString();

                Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);

                boolean image = false;

                try {
                    multipartFile.transferTo(savePath); // 실제 파일 저장

                    // 이미지 파일의 종류라면
                    if(Files.probeContentType(savePath).startsWith("image")) {

                        image = true;

                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);

                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                list.add(UploadResultDto.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(image)
                        .build()
                );

            }); // end each

            return list.toString();

        } // end if

        return null;

    }

}
