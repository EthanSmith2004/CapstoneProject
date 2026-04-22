package com.jel.spys.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
@Scope("singleton")
public class ClockService {

    private Clock clock = Clock.systemDefaultZone();

    public Instant now() {
        return Instant.now(clock);
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    public void setFixedTime(Instant fixedDateTime) {
        this.clock = Clock.fixed(fixedDateTime, ZoneId.systemDefault());
    }

    public void advanceBy(Duration duration) {
        Instant newInstant = clock.instant().plus(duration);
        this.clock = Clock.fixed(newInstant, ZoneId.systemDefault());
    }

    public void reset() {
        this.clock = Clock.systemDefaultZone();
    }
}
