package com.example.mind_laundromat.cbt.dto;

import com.example.mind_laundromat.cbt.entity.EmotionType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SelectCbtResponse {
    private Long user_id;

    /**
     * Diary
     * */
    private Long diary_id;

    private String before_content; // 교정 전

    private String after_content; // 교정 후

    private LocalDateTime regDate; // 생성 시간

    private LocalDateTime modDate; // 수정 시간

    /**
     * Emotion
     * */
    private EmotionType emotion_type; // 감정 종류


    /**
     * Feedback
     * */
    private String summation; // 요약
}
