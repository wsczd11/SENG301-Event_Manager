# SENG301 Assignment 1 (2021) - Student answers

**Zhedong CAO**

## Task 1.b - Write acceptance tests for U3 - Add participants to events

### Feature file (Cucumber Scenarios)

**participant.feature**

### Java class implementing the acceptance tests

**AddParticipantsFeature.java**

## Task 2 - Identify the patterns in the code

### Pattern 1

#### What pattern is it?

**State**

#### What is its goal in the code?

**Allow event have different behaviour when they have different type.**

#### Name of UML Class diagram attached:

**State_UML.png**

#### Mapping to GoF pattern elements:

| GoF element | Code element |
| ----------- | ------------ |
| Contest        | EventHandlerImpl    |
| Request        | updateEventStatus() |
| State          | Event               |
| ConcreteStateA | ArchivedEvent       |
| ConcreteStateB | CanceledEvent       |
| ConcreteStateC | PastEvent           |
| ConcreteStateD | ScheduledEvent      |
| handle()       | toString()          |


### Pattern 2

#### What pattern is it?

**Factory Method**

#### What is its goal in the code?

**User can creat an event without know the process of create a user.**

#### Name of UML Class diagram attached:

**Singleton_UML.png**

#### Mapping to GoF pattern elements:

| GoF element | Code element |
| ----------- | ------------ |
| AbstractCreator | EventHandler     |
| ConcreteCreator | EventHandlerImpl |
| AbstractProduct | Event            |
| ConcreteProduct | ScheduledEvent   |
| FactoryMethod   | createEvent()    |

## Task 3 - Full UML Class diagram

### Name of file of full UML Class diagram attached

**Full_UML.png**

## Task 4 - Implement new feature

### What pattern fulfils the need for the feature?

**Observer Pattern**

### What is its goal and why is it needed here?

**Send a notice to observer when the subject been update. The reason of I use it here is that the individual user can get notice when Event change status.**

### Name of UML Class diagram attached:

**Observer_UML.png**

### Mapping to GoF pattern elements:

| GoF element | Code element |
| ----------- | ------------ |
| Subject                                        | Event                                                   |
| Observer                                       | Participant                                             |
| Concrete Subject                               | ArchivedEvent, CanceledEvent, PastEvent, ScheduledEvent |
| Concrete Observer                              | Participant                                             |
| doSomething()                                  | cancel(), happen(), reschedule(), archive()             |
| getter()                                       | sendNotification(Event event)                                              |
| subject - Observer Relations                   | Association                                             |
| Concrete subject - Concrete Observer Relations | Association                                             |

## Task 5 - BONUS - Acceptance tests for Task 4

### Feature file (Cucumber Scenarios)

**status.feature**

### Java class implementing the acceptance tests

**ChangeStatus.java**
