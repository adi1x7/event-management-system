package com.example.eventsysten.controller;

import com.example.eventsysten.entity.Feedback;
import com.example.eventsysten.entity.Event;
import com.example.eventsysten.repository.FeedbackRepository;
import com.example.eventsysten.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/form/{eventId}")
    public String showFeedbackForm(@PathVariable Long eventId, Model model) {
        try {
            Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
            model.addAttribute("event", event);
            model.addAttribute("feedback", new Feedback());
            return "feedback-form";
        } catch (RuntimeException e) {
            logger.error("Error showing feedback form for event {}: {}", eventId, e.getMessage());
            // Optionally, redirect to an error page or event list with an error message
            return "redirect:/events"; // Redirect to events page on error
        }
    }

    @PostMapping("/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback, @RequestParam Long eventId) {
        logger.info("Received feedback submission for event: {}", eventId);

        // Fetch the event using the separately provided eventId
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        feedback.setEvent(event); // Set the fetched Event object on the feedback
        feedbackRepository.save(feedback);

        logger.info("Feedback saved successfully: {}", feedback);
        return "redirect:/events"; // Redirect to events page
    }

    @GetMapping("/thank-you")
    public String thankYou() {
        return "feedback-thank-you";
    }

    @GetMapping("/list/{eventId}")
    public String listFeedback(@PathVariable Long eventId, Model model) {
        try {
            Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
            List<Feedback> feedbacks = feedbackRepository.findByEventId(eventId);

            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("event", event);
            return "feedback-list";
        } catch (RuntimeException e) {
            logger.error("Error listing feedback for event {}: {}", eventId, e.getMessage());
            return "redirect:/events";
        }
    }

    @GetMapping("/list")
    public String listAllFeedback(Model model) {
        try {
            logger.info("Fetching all feedback");
            List<Feedback> allFeedback = feedbackRepository.findAll();
            List<Event> events = eventRepository.findAll();
            
            model.addAttribute("feedbacks", allFeedback);
            model.addAttribute("events", events);
            return "feedback-list";
        } catch (Exception e) {
            logger.error("Error listing all feedback: {}", e.getMessage());
            return "redirect:/admin";
        }
    }
} 