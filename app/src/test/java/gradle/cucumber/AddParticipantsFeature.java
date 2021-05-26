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

    private Event event;
    private Long eventId;
    private Participant participant;
    private List<Participant> participants;

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
        event = eventHandler.createEvent(name, description, date, type);
        eventId = eventAccessor.persistEvent(event);
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
        event.addParticipant(participant);
        eventAccessor.persistEventAndParticipants(event);
    }

    @Then("The participant {string} has been add to event {string}")
    public void the_participant_has_been_add_to_event(String name, String eventName) {
        Event anEvent = eventAccessor.getEventAndParticipantsById(eventId);
        Assertions.assertEquals(anEvent.getName(), eventName);
        List<Participant> participants = anEvent.getParticipants();

        boolean flag = false;
        for (Participant anParticipant : participants) {
            System.out.println(anParticipant.getName());
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
        event.addParticipant(new Participant(name));
        eventAccessor.persistEventAndParticipants(event);
    }

    //
    // U3 - AC3
    //

    @Given("A list of participant")
    public void a_list_of_participant() {
        participants = new ArrayList<>();
    }

    @When("I do not add any participant into list")
    public void i_do_not_add_any_participant_into_list() {
        Assertions.assertEquals(0, participants.size());
    }

    @When("I do add a participant {string} into list")
    public void i_do_add_a_participant_into_list(String name) {
        participants.add(new Participant(name));
    }

    @Then("I expect an exception that disallow me to add participants")
    public void i_expect_an_exception_that_disallow_me_to_add_participants() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                eventHandler.addParticipants(event, participants));
    }
}
