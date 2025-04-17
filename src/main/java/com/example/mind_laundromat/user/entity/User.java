package com.example.mind_laundromat.user.entity;

import com.example.mind_laundromat.cbt.entity.Diary;
import com.example.mind_laundromat.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender; //성별 [MALE, FEMALE]

    @OneToMany(mappedBy = "user")
    private List<Diary> diaryList = new ArrayList<>();



}
