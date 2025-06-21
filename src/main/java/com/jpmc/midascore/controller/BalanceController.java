package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.service.BalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("")
    public ResponseEntity<Balance> balance(@RequestParam String userId) {
        Balance balance = balanceService.getUserBalance(Long.parseLong(userId));
        return ResponseEntity.ok(balance);
    }
}