package com.example.mind_laundromat.cbt.service;

import com.example.mind_laundromat.cbt.dto.CreateCbtRequest;
import com.example.mind_laundromat.cbt.dto.SelectCbtResponse;
import com.example.mind_laundromat.cbt.entity.Diary;
import com.example.mind_laundromat.cbt.entity.Emotion;
import com.example.mind_laundromat.cbt.entity.Feedback;
import com.example.mind_laundromat.cbt.repository.DiaryRepository;
import com.example.mind_laundromat.cbt.repository.EmotionRepository;
import com.example.mind_laundromat.cbt.repository.FeedbackRepository;
import com.example.mind_laundromat.user.entity.User;
import com.example.mind_laundromat.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CbtService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final EmotionRepository emotionRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public void createCbt(CreateCbtRequest createCbtRequest) {

        // 1. User 정보 확인
        User user = userRepository.findById(createCbtRequest.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. Diary 저장
        Diary diary = Diary.builder()
                .user(user)
                .before_content(createCbtRequest.getBefore_content())
                .after_content(createCbtRequest.getAfter_content())
                .build();
        Diary saveDiary = diaryRepository.save(diary);

        // 3. Emotion 저장
        Emotion emotion = Emotion.builder()
                .emotion_type(createCbtRequest.getEmotion_type())
                .level(createCbtRequest.getLevel())
                .build();
        Emotion saveEmotion = emotionRepository.save(emotion);

        // 4. Feedback 저장
        Feedback feedback = Feedback.builder()
                .recommend_content(createCbtRequest.getRecommend_content())
                .content(createCbtRequest.getContent())
                .build();
        Feedback saveFeedback = feedbackRepository.save(feedback);

        // 5. Diary에 Emotion, Feedback 연결
        saveDiary.setEmotion(saveEmotion);
        saveDiary.setFeedback(saveFeedback);

        diaryRepository.save(saveDiary);
    }

    public SelectCbtResponse selectCbt(Long diary_id) {

        // 1. Diary 정보 가져오기
        Diary diary = diaryRepository.findById(diary_id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다."));

        // 2. dto 변환
        SelectCbtResponse response = SelectCbtResponse.builder()
                .user_id(diary.getUser().getUser_id())
                .diary_id(diary.getDiary_id())
                .before_content(diary.getBefore_content())
                .after_content(diary.getAfter_content())
                .regDate(diary.getRegDate())
                .modDate(diary.getModDate())
                .emotion_type(diary.getEmotion().getEmotion_type())
                .level(diary.getEmotion().getLevel())
                .recommend_content(diary.getFeedback().getRecommend_content())
                .content(diary.getFeedback().getContent())
                .build();

        return response;
    }
}
