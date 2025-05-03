package com.example.mind_laundromat.cbt.controller;

import com.example.mind_laundromat.cbt.dto.CreateCbtRequest;
import com.example.mind_laundromat.cbt.dto.SelectCbtResponse;
import com.example.mind_laundromat.cbt.service.CbtService;
import com.example.mind_laundromat.response.CommonResponse;
import com.example.mind_laundromat.response.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cbt")
@Log4j2
@RequiredArgsConstructor // 자동 주입을 위한 애노테이션
public class CbtController {

    private final CbtService cbtService;

    @PostMapping()
    public ResponseEntity<CommonResponse<String>> createBoard(@RequestBody CreateCbtRequest createCbtRequest) {
        cbtService.createCbt(createCbtRequest);
        return ResponseEntity.ok(ResponseBuilder.success("등록하였습니다."));
    }

    @GetMapping("/{diary_id}")
    public ResponseEntity<CommonResponse<SelectCbtResponse>> selectCbt(@PathVariable("diary_id") Long diary_id) {
        return ResponseEntity.ok(ResponseBuilder.success(cbtService.selectCbt(diary_id)));
    }

    @DeleteMapping("/{diary_id}")
    public ResponseEntity<CommonResponse<String>> deleteCbt(@PathVariable("diary_id") Long diary_id) {
        cbtService.deleteCbt(diary_id);
        return ResponseEntity.ok(ResponseBuilder.success("삭제하였습니다."));
    }

}

