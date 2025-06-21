package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Balance;

public interface BalanceService {
    public Balance getUserBalance(long userId);
}