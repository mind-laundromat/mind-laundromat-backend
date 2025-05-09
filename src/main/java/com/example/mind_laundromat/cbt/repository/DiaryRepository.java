package com.example.mind_laundromat.cbt.repository;

import com.example.mind_laundromat.cbt.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByUserUserIdAndRegDateBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
