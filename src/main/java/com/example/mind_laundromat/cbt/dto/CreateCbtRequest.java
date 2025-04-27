package com.example.mind_laundromat.cbt.dto;

import com.example.mind_laundromat.cbt.entity.EmotionType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateCbtRequest {
    /**
     * User
     * */
    private Long user_id;

    /**
     * Diary
     * */
    private String before_content; //교정 전

    private String after_content; //교정 후

    /**
     * Emotion
     * */
    private EmotionType emotion_type; // 감정 종류

    private int level; // [1 ~ 10]

    /**
     * Feedback
     * */
    private String recommend_content;

    private String content;

}
