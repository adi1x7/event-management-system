package com.example.eventsysten.repository;

import com.example.eventsysten.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByEventId(Long eventId);
    List<Payment> findByRegistrationId(Long registrationId);
    List<Payment> findByStatus(String status);
} 