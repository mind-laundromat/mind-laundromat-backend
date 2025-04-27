package com.example.mind_laundromat.cbt.repository;

import com.example.mind_laundromat.cbt.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
