package com.example.eventsysten.controller;

import com.example.eventsysten.entity.Event;
import com.example.eventsysten.entity.Registration;
import com.example.eventsysten.entity.TicketType;
import com.example.eventsysten.entity.Payment;
import com.example.eventsysten.repository.EventRepository;
import com.example.eventsysten.repository.RegistrationRepository;
import com.example.eventsysten.repository.TicketTypeRepository;
import com.example.eventsysten.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    // Landing page
    @GetMapping("/")
    public String home() {
        return "index"; // Return index.html
    }

    // User events page (renders events.html)
    @GetMapping("/events")
    public String viewEvents(Model model) {
        logger.info("Fetching events for user display");
        List<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);
        return "events"; // User view
    }

    // Admin dashboard page (renders admin.html)
    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin";
    }

    // Admin events list page (renders admin-events.html)
    @GetMapping("/admin/events")
    public String viewAdminEvents(Model model) {
        logger.info("Fetching events for admin display");
        List<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);
        return "admin-events"; // Admin view
    }

    // Show create event form (linked from admin)
    @GetMapping("/events/create")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "create-event";
    }

    // Handle create event form submission
    @PostMapping("/events/create")
    public String handleCreateEventForm(@ModelAttribute Event event) {
        eventRepository.save(event); // Save the event first to get its ID

        // Save associated ticket types
        if (event.getTicketTypes() != null) {
            for (TicketType ticketType : event.getTicketTypes()) {
                if (ticketType.getName() != null && !ticketType.getName().trim().isEmpty()) { // Only save valid ticket types
                    ticketType.setEvent(event); // Link ticket type to the newly created event
                    ticketTypeRepository.save(ticketType);
                }
            }
        }
        return "redirect:/admin/events"; // Redirect admin back to admin event list
    }

    // Show registration form (linked from user events page)
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Fetching data for registration form");
        List<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);

        List<TicketType> ticketTypes = List.of(); // Default empty list
        if (!events.isEmpty()) {
            // Fetch ticket types for the first event if events exist
            ticketTypes = ticketTypeRepository.findByEventId(events.get(0).getId());
        }
        model.addAttribute("ticketTypes", ticketTypes);
        
        // Add a new Registration object for form binding
        model.addAttribute("registration", new Registration());

        return "register";
    }

    // Handle registration form submission
    @PostMapping("/register")
    public String handleRegistrationForm(
            @RequestParam("event.id") Long eventId,
            @RequestParam("ticketType.id") Long ticketTypeId,
            @RequestParam("numberOfTickets") Integer numberOfTickets,
            @RequestParam("attendeeName") String attendeeName,
            @RequestParam("attendeeEmail") String attendeeEmail,
            Model model) {

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
            .orElseThrow(() -> new RuntimeException("Ticket type not found"));

        Double totalPrice = ticketType.getPrice() * numberOfTickets;

        Registration registration = new Registration();
        registration.setEvent(event);
        registration.setTicketType(ticketType);
        registration.setNumberOfTickets(numberOfTickets);
        registration.setAttendeeName(attendeeName);
        registration.setAttendeeEmail(attendeeEmail);
        registration.setTotalPrice(totalPrice);
        registrationRepository.save(registration);

        // Create payment record
        Payment payment = new Payment();
        payment.setAmount(totalPrice);
        payment.setPaymentMethod("Credit Card");
        payment.setRegistration(registration);
        payment.setEvent(event);
        payment.setStatus("PENDING");
        paymentRepository.save(payment);

        model.addAttribute("message", "Registration successful!");
        return "redirect:/events"; // Redirect user back to user event list
    }

    // API: Get all events (for dynamic forms, etc.)
    @GetMapping("/api/events")
    @ResponseBody
    public List<Event> getEvents() {
        logger.info("Fetching all events (API)");
        return eventRepository.findAll();
    }

    // API: Create new event (existing API, not used by new form flow)
    @PostMapping("/api/events")
    @ResponseBody
    public Event createEventApi(@RequestBody Event event) {
        logger.info("Creating new event (API): {}", event.getName());
        return eventRepository.save(event);
    }

    // API: Create ticket type for an event
    @PostMapping("/api/events/{eventId}/ticket-types")
    @ResponseBody
    public TicketType createTicketType(@PathVariable Long eventId, @RequestBody TicketType ticketType) {
        logger.info("Creating ticket type for event {} (API): name={}, price={}", 
            eventId, ticketType.getName(), ticketType.getPrice());
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> {
                logger.error("Event not found with id: {}", eventId);
                return new RuntimeException("Event not found");
            });
        
        if (ticketType.getPrice() == null || ticketType.getPrice() <= 0) {
            logger.error("Invalid price for ticket type: {}", ticketType.getPrice());
            throw new RuntimeException("Ticket price must be greater than 0");
        }
        
        ticketType.setEvent(event);
        TicketType savedTicketType = ticketTypeRepository.save(ticketType);
        logger.info("Successfully created ticket type (API): {}", savedTicketType);
        return savedTicketType;
    }

    // API: Get ticket types for an event
    @GetMapping("/api/events/{eventId}/ticket-types")
    @ResponseBody
    public List<TicketType> getTicketTypes(@PathVariable Long eventId) {
        logger.info("Fetching ticket types for event {} (API)", eventId);
        return ticketTypeRepository.findByEventId(eventId);
    }

    // View registrations page (renders registrations.html)
    @GetMapping("/admin/registrations")
    public String viewRegistrations(Model model) {
        try {
            logger.info("Starting to fetch registrations for admin display");
            List<Registration> registrations = registrationRepository.findAll();
            logger.info("Found {} registrations", registrations.size());
            
            // Fetch payments for each registration
            for (Registration registration : registrations) {
                try {
                    logger.info("Fetching payments for registration ID: {}", registration.getId());
                    List<Payment> payments = paymentRepository.findByRegistrationId(registration.getId());
                    logger.info("Found {} payments for registration ID: {}", payments.size(), registration.getId());
                    registration.setPayments(payments);
                    
                    // Log registration details for debugging
                    logger.info("Registration details - ID: {}, Event: {}, Attendee: {}, Payments: {}", 
                        registration.getId(),
                        registration.getEvent() != null ? registration.getEvent().getName() : "null",
                        registration.getAttendeeName(),
                        payments.size());
                } catch (Exception e) {
                    logger.error("Error processing registration ID {}: {}", registration.getId(), e.getMessage(), e);
                }
            }
            
            model.addAttribute("registrations", registrations);
            logger.info("Successfully added registrations to model");
            return "registrations"; // Admin view
        } catch (Exception e) {
            logger.error("Error in viewRegistrations: {}", e.getMessage(), e);
            throw e; // Re-throw to see the full stack trace
        }
    }

    // API: Get all registrations (for AJAX calls if needed)
    @GetMapping("/api/events/registrations")
    @ResponseBody
    public List<Registration> getRegistrationsApi() {
        logger.info("Fetching all registrations (API)");
        List<Registration> registrations = registrationRepository.findAll();
        logger.info("Found {} registrations (API)", registrations.size());
        return registrations;
    }

    // API: Delete an event and its related entities
    @DeleteMapping("/api/events/{eventId}")
    @ResponseBody
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        logger.info("Attempting to delete event with ID: {}", eventId);
        
        try {
            Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

            // Delete the event - cascading will handle related entities
            eventRepository.delete(event);
            logger.info("Successfully deleted event {} and all related entities", eventId);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting event {}: {}", eventId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting event: " + e.getMessage());
        }
    }

    // Admin: Show page to manage ticket types for an event
    @GetMapping("/admin/events/{eventId}/ticket-types")
    public String manageTicketTypes(@PathVariable Long eventId, Model model) {
        logger.info("Displaying manage ticket types page for event ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        model.addAttribute("ticketType", new TicketType()); // For the add form
        model.addAttribute("ticketTypes", ticketTypeRepository.findByEventId(eventId)); // Existing ticket types
        return "manage-ticket-types";
    }

    // Admin: Handle adding a new ticket type for an event
    @PostMapping("/admin/events/{eventId}/ticket-types/add")
    public String addTicketType(@PathVariable Long eventId, @ModelAttribute TicketType ticketType) {
        logger.info("Adding new ticket type for event {}: {}", eventId, ticketType.getName());
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        ticketType.setEvent(event);
        ticketTypeRepository.save(ticketType);
        return "redirect:/admin/events/" + eventId + "/ticket-types";
    }

    // Admin: Show form to edit a ticket type
    @GetMapping("/admin/ticket-types/edit/{id}")
    public String showEditTicketTypeForm(@PathVariable Long id, Model model) {
        logger.info("Displaying edit ticket type form for ID: {}", id);
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket type not found"));
        model.addAttribute("ticketType", ticketType);
        model.addAttribute("event", ticketType.getEvent()); // Pass the associated event for the page title
        return "edit-ticket-type";
    }

    // Admin: Handle editing a ticket type
    @PostMapping("/admin/ticket-types/edit/{id}")
    public String handleEditTicketTypeForm(@PathVariable Long id, @ModelAttribute TicketType ticketType) {
        logger.info("Updating ticket type with ID {}: {}", id, ticketType.getName());
        // Fetch existing ticket type to ensure we don't lose the event relationship if not passed in form
        TicketType existingTicketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket type not found"));

        existingTicketType.setName(ticketType.getName());
        existingTicketType.setPrice(ticketType.getPrice());
        existingTicketType.setQuantity(ticketType.getQuantity());
        existingTicketType.setDescription(ticketType.getDescription());

        ticketTypeRepository.save(existingTicketType);
        // Redirect back to the manage ticket types page for the specific event
        return "redirect:/admin/events/" + existingTicketType.getEvent().getId() + "/ticket-types";
    }

    // Admin: Handle deleting a ticket type
    @PostMapping("/admin/ticket-types/delete/{id}")
    public String deleteTicketType(@PathVariable Long id) {
        logger.info("Attempting to delete ticket type with ID: {}", id);
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket type not found"));

        Long eventId = ticketType.getEvent().getId(); // Get event ID before deleting ticket type
        ticketTypeRepository.deleteById(id);
        logger.info("Successfully deleted ticket type {}", id);
        return "redirect:/admin/events/" + eventId + "/ticket-types";
    }
}
