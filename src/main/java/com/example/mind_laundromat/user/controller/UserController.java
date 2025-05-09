package com.example.mind_laundromat.user.controller;

import com.example.mind_laundromat.response.CommonResponse;
import com.example.mind_laundromat.response.ResponseBuilder;
import com.example.mind_laundromat.user.dto.UserDTO;
import com.example.mind_laundromat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Log4j2
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<String>> signup(@RequestBody UserDTO userDTO) {
        userService.registerUser(userDTO);

        return ResponseEntity.ok(ResponseBuilder.success("회원가입에 성공하였습니다."));
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<CommonResponse<UserDTO>> getUser(@PathVariable("user_id") Long user_id) {
        return ResponseEntity.ok(ResponseBuilder.success(userService.findByUserId(user_id)));
    }
}
