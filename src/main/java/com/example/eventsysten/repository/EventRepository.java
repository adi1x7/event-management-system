package com.example.eventsysten.repository;


import com.example.eventsysten.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
