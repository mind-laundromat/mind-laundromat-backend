package com.example.mind_laundromat.user.repository;

import com.example.mind_laundromat.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
