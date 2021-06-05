package gradle.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import uc.seng301.eventapp.accessor.EventAccessor;
import uc.seng301.eventapp.accessor.ParticipantAccessor;
import uc.seng301.eventapp.handler.EventHandler;
import uc.seng301.eventapp.handler.EventHandlerImpl;
import uc.seng301.eventapp.model.Event;
import uc.seng301.eventapp.model.EventStatus;
import uc.seng301.eventapp.model.Participant;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class ChangeStatus {
    private SessionFactory sessionFactory;
    private EventAccessor eventAccessor;
    private EventHandler eventHandler;
    private ParticipantAccessor participantAccessor;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private Event givenEvent;
    private Long eventId;
    private EventStatus eventStatus;

    @Before
    public void setup() {
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
        eventAccessor = new EventAccessor(sessionFactory, new ParticipantAccessor(sessionFactory));
        eventHandler = new EventHandlerImpl(eventAccessor);
        participantAccessor = new ParticipantAccessor(sessionFactory);
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void clean() {
        System.setOut(null);
    }

    //
    // Background
    //

    @Given("One event with name {string}, description {string}, type {string} and date {string}")
    public void tone_event_with_name_description_type_and_date(String name, String description, String type,
                                                                      String date) {
        givenEvent = eventHandler.createEvent(name, description, date, type);
        eventId = eventAccessor.persistEvent(givenEvent);
    }

    @Given("participant {string} and participant {string} has been add to this event")
    public void participant_and_participant_has_been_add_to_this_event(String name, String otherName) {
        eventHandler.addParticipants(givenEvent, List.of(new Participant(name), new Participant(otherName)));
        Long newEventId = eventAccessor.persistEventAndParticipants(givenEvent);
        Assertions.assertTrue(newEventId == eventId);
    }

    //
    // U4 - AC1
    //

    @Given("The current status is {string}")
    public void the_current_states_is(String status) {
        Assertions.assertEquals(status, givenEvent.toString().split("[ ]")[0]);
    }

    @When("the status change to PAST")
    public void the_states_change_to() {
        eventStatus =  EventStatus.PAST;
        givenEvent = eventHandler.updateEventStatus(givenEvent, eventStatus, null);
    }

    @Then("Every participants who attend this event will receive a notification contain the name and new status of this event.")
    public void every_participants_who_attend_this_event_will_receive_a_notification_contain_the_name_and_new_status_of_this_event() {
        for (Participant participant: givenEvent.getParticipants()){
            Assertions.assertEquals(givenEvent.getName(), participant.getReceiveEventName());
            Assertions.assertEquals(eventStatus.toString(), participant.getReceiveNewEventStatus());
        }
    }

    //
    // U4 - AC2
    //

    @Given("I receive the notification")
    public void i_receive_the_notification() {
        for (Participant participant: givenEvent.getParticipants()){
            Assertions.assertNotEquals(null, participant.getReceiveEventName());
            Assertions.assertNotEquals(null, participant.getReceiveNewEventStatus());
        }
    }

    @Then("A message containing my name, the event name and the new status {string} will been print.")
    public void a_message_containing_my_name_the_event_name_and_the_new_status_of_the_event_will_been_print(String newStatus) {
        String form = "Hi %s, the status of Event SENG301 Lab has been change to %s.\n";
        String output = "";
        for (Participant participant: givenEvent.getParticipants()){
            output += String.format(form, participant.getName(), newStatus);
        }
        Assertions.assertEquals(output, outContent.toString());
    }

    //
    // U4 - AC3
    //

    @When("I receive the printing message contain new status {string}, and I want to delete myself from this event, my name is {string}")
    public void i_receive_the_notification_and_i_want_to_delete_myself_from_this_event(String newStatus, String name) {
        String form = "Hi %s, the status of Event SENG301 Lab has been change to %s.\n";
        String output = "";
        for (Participant participant: givenEvent.getParticipants()){
            output += String.format(form, participant.getName(), newStatus);
        }
        Assertions.assertEquals(output, outContent.toString());
        for (Participant participant: givenEvent.getParticipants()){
            if (participant.getName().equals(name)){
                givenEvent.deleteOneParticipants(participant);
            }
        }
    }

    @Then("I have been removed in current event, my name is {string}")
    public void i_have_been_removed_in_current_event_my_name_is(String name) {
        for (Participant participant: givenEvent.getParticipants()){
            Assertions.assertNotEquals(name, participant.getName());
        }
    }

    //
    // U4 - AC4
    //

    @When("the status change to ARCHIVED")
    public void the_states_change_to_archived() {
        eventStatus =  EventStatus.ARCHIVED;
        givenEvent = eventHandler.updateEventStatus(givenEvent, eventStatus, null);
    }

    @Then("No participant in this event")
    public void no_participant_in_this_event() {
        Assertions.assertTrue(givenEvent.getParticipants().isEmpty());
    }

}
