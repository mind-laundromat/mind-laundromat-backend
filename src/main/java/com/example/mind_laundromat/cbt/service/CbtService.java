package com.example.mind_laundromat.cbt.service;

import com.example.mind_laundromat.cbt.dto.CreateCbtRequest;
import com.example.mind_laundromat.cbt.dto.SelectCbtListRequest;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CbtService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final EmotionRepository emotionRepository;
    private final FeedbackRepository feedbackRepository;

    // CREATE CBT
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
                .build();
        Emotion saveEmotion = emotionRepository.save(emotion);

        // 4. Feedback 저장
        Feedback feedback = Feedback.builder()
                .summation(createCbtRequest.getSummation())
                .build();
        Feedback saveFeedback = feedbackRepository.save(feedback);

        // 5. Diary에 Emotion, Feedback 연결
        saveDiary.setEmotion(saveEmotion);
        saveDiary.setFeedback(saveFeedback);

        diaryRepository.save(saveDiary);
    }

    // SELECT CBT
    public SelectCbtResponse selectCbt(Long diary_id) {

        // 1. Diary 정보 가져오기
        Diary diary = diaryRepository.findById(diary_id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다."));

        // 2. dto 변환
        return SelectCbtResponse.builder()
                .user_id(diary.getUser().getUserId())
                .diary_id(diary.getDiary_id())
                .before_content(diary.getBefore_content())
                .after_content(diary.getAfter_content())
                .regDate(diary.getRegDate())
                .modDate(diary.getModDate())
                .emotion_type(diary.getEmotion().getEmotion_type())
                .summation(diary.getFeedback().getSummation())
                .build();
    }

    // CREATE CBT List
    public List<SelectCbtResponse> selectCbtList(SelectCbtListRequest selectCbtListRequest) {

        // 날짜 범위 설정
        LocalDateTime startOfDay = selectCbtListRequest.getLocalDate().atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = selectCbtListRequest.getLocalDate().atTime(LocalTime.MAX); // 23:59:59.999999999

        // Diary 리스트 조회
        List<Diary> diaries = diaryRepository.findByUserUserIdAndRegDateBetween(selectCbtListRequest.getUser_id(), startOfDay, endOfDay);

        // Diary -> SelectCbtResponse DTO로 변환
        return diaries.stream()
                .map(SelectCbtResponse::new)
                .collect(Collectors.toList());
    }

    // DELETE CBT
    public void deleteCbt(Long diary_id) {

        // 1. Diary 정보 가져오기
        Diary diary = diaryRepository.findById(diary_id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다."));

        // 2. 삭제
        diaryRepository.delete(diary);
    }
}
