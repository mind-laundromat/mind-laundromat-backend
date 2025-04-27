package com.example.mind_laundromat.user.dto;

import com.example.mind_laundromat.cbt.entity.Diary;
import com.example.mind_laundromat.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {

    private Long user_id;

    private String email;

    private String password;

    private String name;

    private Gender gender; //성별 [MALE, FEMALE]

    private List<Diary> diaryList = new ArrayList<>();
}
