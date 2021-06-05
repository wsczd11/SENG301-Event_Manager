/*
 * Created on Wed Apr 07 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package uc.seng301.eventapp;

import java.util.*;
import java.util.zip.DataFormatException;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import uc.seng301.eventapp.accessor.EventAccessor;
import uc.seng301.eventapp.accessor.ParticipantAccessor;
import uc.seng301.eventapp.handler.EventHandler;
import uc.seng301.eventapp.handler.EventHandlerImpl;
import uc.seng301.eventapp.location.LocationService;
import uc.seng301.eventapp.location.LocationServiceResult;
import uc.seng301.eventapp.location.NominatimQuery;
import uc.seng301.eventapp.model.Event;
import uc.seng301.eventapp.model.EventStatus;
import uc.seng301.eventapp.model.Location;
import uc.seng301.eventapp.model.Participant;
import uc.seng301.eventapp.util.DateUtil;

/**
 * This class is the entry point of assignment 3. It defines a simple Command
 * Line Interface (CLI) app to create events and register participants. You will
 * be expected to make changes in this CLI to handle the new user story.
 */
public class App {

  private final SessionFactory sessionFactory;
  private final LocationService locationService;

  private final EventHandler eventHandler;
  private final EventAccessor eventAccessor;
  private final ParticipantAccessor participantAccessor;

  private final Scanner cli;

  private static final Logger LOGGER = LogManager.getLogger(App.class);

  /**
   * Default constructor. Initialise the JPA session factory
   */
  public App() {
    // this will load the config file (xml file in resources folder)
    Configuration configuration = new Configuration();
    configuration.configure();
    sessionFactory = configuration.buildSessionFactory();

    participantAccessor = new ParticipantAccessor(sessionFactory);
    eventAccessor = new EventAccessor(sessionFactory, participantAccessor);
    eventHandler = new EventHandlerImpl(eventAccessor);

    cli = new Scanner(System.in);
    locationService = new NominatimQuery();
  }

  /**
   * Main entry point of the App. Does not expect any argument
   *
   * @param args none expected
   */
  public static void main(String[] args) {
    App app = new App();

    app.runCli();
  }

  /*----------------
  * PRIVATE METHODS
  ----------------*/

  /**
   * Main entry point for the command line interface
   */
  private void runCli() {
    welcome();

    boolean quit = false;
    while (!quit) {
      mainMenu();
      String input = cli.nextLine();
      int menuItem;
      try {
        menuItem = Integer.parseInt(input);
      } catch (NumberFormatException e) {
        menuItem = -1;
      }
      switch (menuItem) {
      case 1:
        addEventMenu();
        break;

      case 2:
        addParticipantsToEventMenu();
        break;

      case 3:
        updateEventStatusMenu();
        break;

      case 4:
        updateCalendar();
        break;

      case 9:
        System.out.println(serializeDatabaseContent(sessionFactory));
        break;

      case 0:
        quit = true;
        System.out.println("See you later!");
        break;

      default:
        System.out.println("Unknown value entered");
      }
    }
  }

  private void welcome() {
    System.out.println("\n######################################################\n\n"
        + "             Welcome to Event Organiser App             \n\n"
        + "########################################################");
  }

  private void mainMenu() {
    // @formatter:off
    System.out.println("\nWhat do you want to do next?\n"
    + "\t 1. Add an event\n"
    + "\t 2. Add a participant to an event\n"
    + "\t 3. Update the status of an event\n"
    + "\t 9. Print database content\n"
    + "\t 0. Exit\n"
    + "\n"
    + "Your Answer: ");
    // @formatter:on
  }

  private void addEventMenu() {
    System.out.println("Enter a name for your event:");
    String name = cli.nextLine();

    System.out.println("Enter a description for your event:");
    String description = cli.nextLine();

    System.out.println(
        "Enter a date for your event in " + DateUtil.getInstance().getDefaultDateFormat().toUpperCase() + " format:");
    String date = cli.nextLine();

    System.out.println("Enter a type for this event:");
    String type = cli.nextLine();

    System.out.println("Enter a cost (can be decimal) for this event (optional, leave blank to ignore):");
    Double cost = 0.0;
    String costString = cli.nextLine();
    if (!costString.isBlank()) {
      try {
        cost = Double.parseDouble(costString);
      } catch (NumberFormatException e) {
        System.out.println("ERROR: given cost is not a valid real number.");
      }
    }

    System.out.println("Enter the location of this event (optional, leave blank to ignore):");
    String locationString = cli.nextLine();
    Location location = null;
    if (!locationString.isBlank()) {
      System.out.println("Do you want to invoke the external location service for " + locationString
          + "? \nType 'yes' to call service, anything else to ignore");
      String response = cli.nextLine();
      if ("yes".equals(response)) {
        LocationServiceResult locationResult = locationService.getCityFromString(locationString);
        if (null != locationResult) {
          location = new Location(locationResult.getName(), locationResult.getLatitude(),
              locationResult.getLongitude());
        } else {
          location = new Location(locationString);
        }
      }
    }

    Event event;
    try {
      event = eventHandler.createEvent(name, description, date, type, cost, location);
    } catch (IllegalArgumentException e) {
      System.out.println("Something is wrong with the values passed: " + e.getMessage());
      return;
    }

    System.out.println("Do you want to add participants? Type 'yes' to add, anything else to finish.");
    if ("yes".equals(cli.nextLine())) {
      addParticipantsToEvent(event);
    }

    try {
      Long eventId = eventAccessor.persistEventAndParticipants(event);
      System.out.println("Event created with id " + eventId);
    } catch (IllegalArgumentException e) {
      System.out.println("Something wrong happened when saving the event: " + e.getMessage());
    }
  }

  private void addParticipantsToEventMenu() {
    System.out.println("What is the id of the event you want to add participant to?");
    String eventId = cli.nextLine();

    if (eventId.chars().allMatch(Character::isDigit)) {
      Event event;
      try{
        event = eventAccessor.getEventAndParticipantsById(Long.parseLong(eventId));
      } catch (NumberFormatException e) {
        System.out.println("Event id wrong or event id not exist.");
        return;
      }
      if (null != event) {
        addParticipantsToEvent(event);
        eventAccessor.persistEventAndParticipants(event);
        System.out.println("Participant/s added to event " + event.getEventId());

      } else {
        System.out.println("Cannot find event with id: " + eventId);
      }
    } else {
      System.out.println("Invalid value passed, needs to be an integer value.");
    }
  }

  private void addParticipantsToEvent(Event event) {
    List<Participant> participants = new ArrayList<>();
    System.out.println("What are the participants' names? Add one name per line and type '!stop' to stop.");
    String name;
    while (!"!stop".equals(name = cli.nextLine())) {
      participants.add(new Participant(name));
    }
    participants.removeIf(participant -> participant.getName().isBlank());
    if (!participants.isEmpty()) {
      eventHandler.addParticipants(event, participants);
    }
  }

  private void updateEventStatusMenu() {
    System.out.println("What is the id of the event you want to update event status to?");
    String eventId = cli.nextLine();
    Event event;
    if (eventId.chars().allMatch(Character::isDigit)) {
      try{
        event = eventAccessor.getEventAndParticipantsById(Long.parseLong(eventId));
      } catch (NumberFormatException e) {
        System.out.println(e.getMessage());
        return;
      }
      if (event == null){
        System.out.println("Wrong event id or event not exist.");
        return;
      }
      EventStatus eventStatus;
      Date date = null;

      // @formatter:off
      System.out.println("\nWhat status do you want to update to?\n"
              + "\t 1. ARCHIVED\n"
              + "\t 2. CANCELED\n"
              + "\t 3. PAST\n"
              + "\t 4. SCHEDULED\n"
              + "\n"
              + "Your Answer: ");
      // @formatter:on

      Integer selectedStatus;
      try{
        selectedStatus = Integer.parseInt(cli.nextLine());
      } catch (NumberFormatException e){
        System.out.println("Input must be integer from 1 to 4");
        return;
      }
      switch (selectedStatus) {
        case 1:
          eventStatus = EventStatus.ARCHIVED;
          break;

        case 2:
          eventStatus = EventStatus.CANCELED;
          break;

        case 3:
          eventStatus = EventStatus.PAST;
          break;

        case 4:
          eventStatus = EventStatus.SCHEDULED;
          System.out.println(
                  "Enter a date for your event in " + DateUtil.getInstance().getDefaultDateFormat().toUpperCase() + " format:");
          String inputDate = cli.nextLine();
          date = DateUtil.getInstance().convertToDate(inputDate);
          Calendar nextYear = Calendar.getInstance();
          nextYear.add(Calendar.YEAR, 1);
          if (date == null || date.before(DateUtil.getInstance().getCurrentDate()) || date.after(nextYear.getTime())) {
            System.out.println(String.format("Date '%s' does not follow expected format %s, is in the past or later than one year",
                                              inputDate, DateUtil.getInstance().getDefaultDateFormat()));
            return;
          }
          break;

        default:
          System.out.println("Input must be integer from 1 to 4");
          return;
      }
      Event newEvent;
      try {
        newEvent = eventHandler.updateEventStatus(event, eventStatus, date);
      } catch (IllegalStateException e) {
        System.out.println(String.format("Current event not allow to change to %s!", eventStatus));
        return;
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
        return;
      }

      if (eventStatus != EventStatus.ARCHIVED) {
          askForAttend(newEvent);
      }

      //pause
//      eventAccessor.persistEventAndParticipants(newEvent);
      try(Session session = sessionFactory.openSession()) {
        Transaction transaction = session.beginTransaction();
        session.createNativeQuery(
                "update event set event_status = '" + eventStatus + "' where id_event = " + newEvent.getEventId())
                .executeUpdate();
        transaction.commit();
      } catch (HibernateException e) {
        LOGGER.error("unable to store / retrieve event type with name '{}'", newEvent.getName(), e);
      }

    } else {
      System.out.println("Invalid value passed, needs to be an integer value.");
    }
  }

  private void updateCalendar() {
    System.out
        .println("Input a new date in " + DateUtil.getInstance().getDefaultDateFormat().toUpperCase() + " format:");
    boolean updated = DateUtil.getInstance().changeCurrentDate(cli.nextLine());
    System.out.println("The date has been updated " + (updated ? "successfully." : "unsuccessfully."));
    if (updated) {
      for (Event event: eventHandler.refreshEvents(sessionFactory)){
          askForAttend(event);
      }
    }
  }

    /**
     * ask user does they still want to join given event
     * @param event changed event
     */
  private void askForAttend(Event event){
    List<Participant> participants = event.getParticipants();
    for (Participant participant: participants){
      System.out.println(String.format("%s, do you still want to join this event? (Yes:'yes'; No:any other keys)",
                  participant.getName()));
      String answer = cli.nextLine();
      if (!answer.equals("yes")){
        event.deleteOneParticipants(participant);
      }
    }
    eventAccessor.persistEvent(event);
  }

  /**
   * Compile the content of all entities stored in the database into user-friendly
   * String.
   *
   * Fully relies on all Entities' toString() method to be fully user-friendly.
   *
   * @return a serialised version of the content of the database.
   */
  private String serializeDatabaseContent(SessionFactory sessionFactory) {
    StringBuilder result = new StringBuilder();
    try (Session session = sessionFactory.openSession()) {
      LOGGER.info("querying all managed entities in database");
      EntityManager manager = sessionFactory.createEntityManager();
      Metamodel metamodel = manager.getMetamodel();
      for (EntityType<?> entityType : metamodel.getEntities()) {
        if ("Event".equals(entityType.getName())) {
          // ignore the Event has it will print all events again
          continue;
        }

        String entityName = entityType.getName();
        result.append("Content of ").append(entityName).append("\n");
        Query<Object> query = session.createQuery("from " + entityName, Object.class);
        LOGGER.info("executing HQL query '{}'", query.getQueryString());
        for (Object o : query.list()) {
          result.append("\t").append(o.toString()).append("\n");
        }
      }

    } catch (HibernateException e) {
      result.append("Couldn't query content because of error").append(e.getLocalizedMessage());
      LOGGER.error("unable to serialize db content. Reason:", e);
    }
    return result.toString();
  }
}
