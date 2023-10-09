package org.zerock.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    private Long tno;

    private String title;

    private LocalDate dueDate;

    private boolean finished;

}
