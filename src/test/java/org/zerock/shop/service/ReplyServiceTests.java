package org.zerock.shop.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.shop.dto.ReplyDto;

@SpringBootTest
@Log4j2
public class ReplyServiceTests {

    @Autowired
    private ReplyService replyService;

    @Test
    public void testRegister() {

        ReplyDto replyDto = ReplyDto.builder()
                .replyText("ReplyDto Text")
                .replyer("replyer")
                .bno(100L)
                .build();

        log.info(replyService.register(replyDto));

    }

}
