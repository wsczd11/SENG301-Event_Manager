package gradle.cucumber;

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
import uc.seng301.eventapp.model.Participant;

import java.util.ArrayList;
import java.util.List;

public class AddParticipantsFeature {
    private SessionFactory sessionFactory;
    private EventAccessor eventAccessor;
    private EventHandler eventHandler;
    private ParticipantAccessor participantAccessor;

    private Event givenEvent;
    private Event retrieveEvent;
    private Long eventId;
    private Participant participant;
    private List<Participant> givenParticipants;
    private List<Participant> retrieveParticipants;

    @Before
    public void setup() {
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
        eventAccessor = new EventAccessor(sessionFactory, new ParticipantAccessor(sessionFactory));
        eventHandler = new EventHandlerImpl(eventAccessor);
        participantAccessor = new ParticipantAccessor(sessionFactory);
    }

    //
    // Background
    //

    @Given("There is one event with name {string}, description {string}, type {string} and date {string}")
    public void there_is_an_event_with_name_description_type_and_date(String name, String description, String type,
                                                                      String date) {
        givenEvent = eventHandler.createEvent(name, description, date, type);
        eventId = eventAccessor.persistEvent(givenEvent);
    }

    //
    // U3 - AC1
    //

    @Given("There is a participant with name {string}")
    public void there_is_a_participant_with_name(String name) {
        Assertions.assertNull(participantAccessor.getParticipantByName(name));
        participant = new Participant(name);
        participantAccessor.persistParticipant(participant);
        Assertions.assertEquals(participantAccessor.getParticipantByName(name).getName(), name);
    }

    @When("I add this participant to the given event")
    public void i_add_this_participant_to_the_given_event() {
        eventHandler.addParticipants(givenEvent, List.of(participant));
        Long newEventId = eventAccessor.persistEventAndParticipants(givenEvent);
        Assertions.assertTrue(newEventId == eventId);
    }

    @Then("The participant {string} has been add to event {string}")
    public void the_participant_has_been_add_to_event(String name, String eventName) {
        retrieveEvent = eventAccessor.getEventAndParticipantsById(eventId);
        Assertions.assertEquals(retrieveEvent.getName(), eventName);
        List<Participant> participants = retrieveEvent.getParticipants();

        boolean flag = false;
        for (Participant anParticipant : participants) {
            if (anParticipant.getName().equals(name)) {
                flag = true;
            }
        }
        Assertions.assertTrue(flag);
    }

    //
    // U3 - AC2
    //

    @Given("There is no participant with name {string}")
    public void there_is_no_participant_with_name(String name) {
        participant = participantAccessor.getParticipantByName(name);
        Assertions.assertNull(participant);
    }

    @When("I add a not exist participant {string} to the given event")
    public void i_add_a_not_exist_participant_to_event(String name) {
        eventHandler.addParticipants(givenEvent, List.of(new Participant(name)));
        Long newEventId = eventAccessor.persistEventAndParticipants(givenEvent);
        Assertions.assertTrue(newEventId == eventId);
    }

    //
    // U3 - AC3
    //

    @Given("A empty list for participants")
    public void a_empty_list_for_participants() {
        givenParticipants = new ArrayList<>();
    }

    @And("add a participant {string} to given list")
    public void add_a_participant_to_given_list(String name) {
        givenParticipants.add(new Participant(name));
    }

    @When("I add given list of participants into event")
    public void i_add_given_list_of_participants_into_event() {
        eventHandler.addParticipants(givenEvent, givenParticipants);
        Long newEventId = eventAccessor.persistEventAndParticipants(givenEvent);
        Assertions.assertTrue(newEventId == eventId);
    }

    @Then("No participant has been add to given event")
    public void no_participant_has_been_add_to_given_event() {
        retrieveEvent = eventAccessor.getEventAndParticipantsById(eventId);
        retrieveParticipants = retrieveEvent.getParticipants();
        Assertions.assertEquals(0, retrieveParticipants.size());
    }
}
