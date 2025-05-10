package com.example.mind_laundromat.cbt.service;

import com.example.mind_laundromat.cbt.dto.CreateCbtRequest;
import com.example.mind_laundromat.cbt.dto.SelectCbtListRequest;
import com.example.mind_laundromat.cbt.dto.SelectCbtResponse;
import com.example.mind_laundromat.cbt.entity.*;
import com.example.mind_laundromat.cbt.repository.*;
import com.example.mind_laundromat.user.entity.User;
import com.example.mind_laundromat.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final DistortionRepository distortionRepository;
    private final DiaryDistortionRepository diaryDistortionRepository;


    // CREATE CBT
    @Transactional
    public void createCbt(CreateCbtRequest createCbtRequest) {

        // 1. User 정보 확인
        User user = userRepository.findByEmail(createCbtRequest.getEmail())
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
                .solution(createCbtRequest.getSolution())
                .build();
        Feedback saveFeedback = feedbackRepository.save(feedback);

        // 5. Diary에 Emotion, Feedback 연결
        saveDiary.setEmotion(saveEmotion);
        saveDiary.setFeedback(saveFeedback);

        saveDiary.setDiaryDistortions(new ArrayList<>());
        System.out.println(saveDiary.getDiaryDistortions());

        // 6.
        for (DistortionType distortionType : createCbtRequest.getDistortions()) {
            Distortion distortion = distortionRepository.findByDistortionType(distortionType)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인지 왜곡 유형입니다: " + distortionType));

            DiaryDistortion diaryDistortion = new DiaryDistortion();
            diaryDistortion.setDiary(saveDiary);
            diaryDistortion.setDistortion(distortion);
            DiaryDistortion saveDiaryDistortion = diaryDistortionRepository.save(diaryDistortion);

            saveDiary.getDiaryDistortions().add(saveDiaryDistortion);
        }

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
                .solution(diary.getFeedback().getSolution())
                .distortion_type(
                        diary.getDiaryDistortions().stream()
                                .map(dd -> dd.getDistortion().getDistortionType())
                                .collect(Collectors.toList())
                )
                .build();
    }

    // SELECT CBT List
    public List<SelectCbtResponse> selectCbtList(SelectCbtListRequest selectCbtListRequest) {

        // 날짜 범위 설정
        LocalDateTime startOfDay = selectCbtListRequest.getLocalDate().atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = selectCbtListRequest.getLocalDate().atTime(LocalTime.MAX); // 23:59:59.999999999

        // Diary 리스트 조회
        List<Diary> diaries = diaryRepository.findByUserEmailAndRegDateBetween(selectCbtListRequest.getEmail(), startOfDay, endOfDay);

        // Diary -> SelectCbtResponse DTO로 변환
        return diaries.stream()
                .map(SelectCbtResponse::new)
                .collect(Collectors.toList());
    }

    // SELECT CBT DATE
    public List<LocalDate> selectCbtDateList(SelectCbtListRequest selectCbtListRequest){

        // LocalDate 추출
        LocalDate localDate = selectCbtListRequest.getLocalDate();

        // 해당 달의 시작일과 마지막일 계산
        LocalDate startDate = localDate.withDayOfMonth(1);
        LocalDate endDate = localDate.withDayOfMonth(localDate.lengthOfMonth());

        // LocalDateTime 범위로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX); // 23:59:59.999999999

        // 해당 범위 내의 Diary 목록을 가져옴
        List<Diary> diaries = diaryRepository.findByUserEmailAndRegDateBetween(selectCbtListRequest.getEmail(), startDateTime, endDateTime);

        // 중복 제거 및 날짜만 추출해서 반환
        return diaries.stream()
                .map(diary -> diary.getRegDate().toLocalDate())
                .distinct()
                .sorted()
                .toList();
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
