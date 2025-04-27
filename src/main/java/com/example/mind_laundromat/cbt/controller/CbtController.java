package com.example.mind_laundromat.cbt.controller;

import com.example.mind_laundromat.cbt.dto.CreateCbtRequest;
import com.example.mind_laundromat.cbt.dto.SelectCbtResponse;
import com.example.mind_laundromat.cbt.service.CbtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cbt")
@Log4j2
@RequiredArgsConstructor // 자동 주입을 위한 애노테이션
public class CbtController {

    private final CbtService cbtService;

    @PostMapping()
    public void createCbt(@RequestBody CreateCbtRequest createCbtRequest) {
        cbtService.createCbt(createCbtRequest);
    }

    @GetMapping("/{diary_id}")
    public SelectCbtResponse selectCbt(@PathVariable("diary_id") Long diary_id) {
        return cbtService.selectCbt(diary_id);
    }

    @DeleteMapping("/{diary_id}")
    public void deleteCbt(@PathVariable("diary_id") Long diary_id) {
        cbtService.deleteCbt(diary_id);
    }

}

