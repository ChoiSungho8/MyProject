package org.zerock.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.shop.dto.upload.UploadFileDto;
import org.zerock.shop.dto.upload.UploadResultDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    @Operation(summary = "view 파일", description = "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);

    }

    @Operation(summary = "remove 파일", description = "DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String, Boolean> removeFile(@PathVariable String fileName) {

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            // 섬네일이 존재한다면
            if(contentType.startsWith("image")) {
                File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);

                thumbnailFile.delete();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        resultMap.put("result", removed);

        return resultMap;

    }

}
