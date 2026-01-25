package learning.itstep.javaweb222.models.event;

import learning.itstep.javaweb222.data.dto.Event;

public class EventBlockModel {

    private Event event;
    private int attendeesCount;

    // ---------------- getters ----------------

    public Event getEvent() {
        return event;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    // ---------------- fluent setters ----------------

    public EventBlockModel setEvent(Event event) {
        this.event = event;
        return this;
    }

    public EventBlockModel setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
        return this;
    }
}
