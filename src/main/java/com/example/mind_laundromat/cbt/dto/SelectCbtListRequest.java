package com.example.mind_laundromat.cbt.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SelectCbtListRequest {

    private Long user_id;

    private LocalDate localDate;
}
