package com.example.mind_laundromat.cbt.entity;

import com.example.mind_laundromat.entity.BaseEntity;
import com.example.mind_laundromat.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diary_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id")
    private Emotion emotion;

    @Column(columnDefinition = "TEXT")
    private String before_content; //교정 전

    @Column(columnDefinition = "TEXT")
    private String after_content; //교정 후

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    @OneToMany(mappedBy = "diary")
    private List<DiaryDistortion> diaryDistortions = new ArrayList<>();

}
