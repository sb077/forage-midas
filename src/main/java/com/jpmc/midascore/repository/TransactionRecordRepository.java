package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {

    @Query("SELECT SUM(tr.amount + tr.incentive) FROM TransactionRecord tr WHERE tr.recipient.id = :userId\n")
    Float sumReceivedAmount(@Param("userId") Long userId);

    @Query("SELECT SUM(tr.amount) FROM TransactionRecord tr WHERE tr.sender.id = :userId")
    Float sumSentAmount(@Param("userId") Long userId);
}
