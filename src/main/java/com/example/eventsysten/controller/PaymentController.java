package com.example.eventsysten.controller;

import com.example.eventsysten.entity.Payment;
import com.example.eventsysten.entity.Event;
import com.example.eventsysten.entity.Registration;
import com.example.eventsysten.repository.PaymentRepository;
import com.example.eventsysten.repository.EventRepository;
import com.example.eventsysten.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @GetMapping("/track/{eventId}")
    public String trackPayments(@PathVariable Long eventId, Model model) {
        logger.info("Fetching payments for event: {}", eventId);
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        List<Payment> payments = paymentRepository.findByEventId(eventId);
        Double totalRevenue = payments.stream()
            .filter(p -> "COMPLETED".equals(p.getStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
        
        model.addAttribute("event", event);
        model.addAttribute("payments", payments);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingPayments", payments.stream().filter(p -> "PENDING".equals(p.getStatus())).count());
        model.addAttribute("completedPayments", payments.stream().filter(p -> "COMPLETED".equals(p.getStatus())).count());
        model.addAttribute("failedPayments", payments.stream().filter(p -> "FAILED".equals(p.getStatus())).count());
        
        return "payment-tracking";
    }

    @PostMapping("/process")
    @ResponseBody
    public String processPayment(@RequestBody Payment payment) {
        logger.info("Processing payment for registration: {}", payment.getRegistration().getId());
        
        try {
            // Generate a unique transaction ID
            payment.setTransactionId(UUID.randomUUID().toString());
            
            // In a real application, you would integrate with a payment gateway here
            // For demo purposes, we'll just mark it as completed
            payment.setStatus("COMPLETED");
            
            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment processed successfully: {}", savedPayment);
            
            return "Payment processed successfully";
        } catch (Exception e) {
            logger.error("Payment processing failed", e);
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            return "Payment processing failed";
        }
    }

    @GetMapping("/registration/{registrationId}")
    public String viewRegistrationPayment(@PathVariable Long registrationId, Model model) {
        List<Payment> payments = paymentRepository.findByRegistrationId(registrationId);
        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found"));
        
        model.addAttribute("payments", payments);
        model.addAttribute("registration", registration);
        return "registration-payments";
    }
} 