package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public TransactionService(UserRepository userRepository,
                              TransactionRecordRepository transactionRecordRepository,
                              RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);

        if (sender == null || recipient == null || sender.getBalance() < transaction.getAmount()) {
            return; // Discard invalid transaction
        }

        // Call Incentive API
        Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive",
                transaction,
                Incentive.class
        );
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);

        // Save transaction record with incentive
        TransactionRecord record = new TransactionRecord(
                sender,
                recipient,
                transaction.getAmount(),
                incentiveAmount,
                Instant.now()
        );

        transactionRecordRepository.save(record);
    }

    // Task 5 methods to compute balance from transactions:

    public float getTotalCredit(Long userId) {
        Float credit = transactionRecordRepository.sumReceivedAmount(userId);
        return credit != null ? credit : 0f;
    }

    public float getTotalDebit(Long userId) {
        Float debit = transactionRecordRepository.sumSentAmount(userId);
        return debit != null ? debit : 0f;
    }
}
