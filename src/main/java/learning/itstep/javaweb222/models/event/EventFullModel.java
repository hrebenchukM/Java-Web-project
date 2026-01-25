package learning.itstep.javaweb222.models.event;

import java.util.List;
import learning.itstep.javaweb222.data.dto.Event;
import learning.itstep.javaweb222.data.dto.EventAttendee;
import learning.itstep.javaweb222.data.dto.EventSchedule;
import learning.itstep.javaweb222.data.dto.EventSpeaker;

public class EventFullModel {

    private Event event;
    private Object organizer; 
    private int attendeesCount;
    private List<EventAttendee> attendees;

    private List<EventSchedule> schedule;
    private List<EventSpeaker> speakers;

    // ---------- getters ----------

    public Event getEvent() {
        return event;
    }

    public Object getOrganizer() {
        return organizer;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    public List<EventAttendee> getAttendees() {
        return attendees;
    }

    public List<EventSchedule> getSchedule() {
        return schedule;
    }

    public List<EventSpeaker> getSpeakers() {
        return speakers;
    }

    // ---------- fluent setters ----------

    public EventFullModel setEvent(Event event) {
        this.event = event;
        return this;
    }
    public EventFullModel setOrganizer(Object organizer) {
        this.organizer = organizer;
        return this;
    }

    public EventFullModel setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
        return this;
    }

    public EventFullModel setAttendees(List<EventAttendee> attendees) {
        this.attendees = attendees;
        return this;
    }

    public EventFullModel setSchedule(List<EventSchedule> schedule) {
        this.schedule = schedule;
        return this;
    }

    public EventFullModel setSpeakers(List<EventSpeaker> speakers) {
        this.speakers = speakers;
        return this;
    }
}
