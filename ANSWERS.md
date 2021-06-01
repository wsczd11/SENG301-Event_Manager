# SENG301 Assignment 1 (2021) - Student answers

**Zhedong CAO**

## Task 1.b - Write acceptance tests for U3 - Add participants to events

### Feature file (Cucumber Scenarios)

**participant.feature**

### Java class implementing the acceptance tests

**AddParticipantsFeature**

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
| Contest        | EventHandler        |
| Request        | updateEventStatus() |
| State          | Event               |
| ConcreteStateA | ArchivedEvent       |
| ConcreteStateB | CanceledEvent       |
| ConcreteStateC | PastEvent           |
| ConcreteStateD | ScheduledEvent      |
| handle()       | toString()          |


### Pattern 2

#### What pattern is it?

**Singleton**

#### What is its goal in the code?

**Reduce memory use and resource occupation.**

#### Name of UML Class diagram attached:

**Singleton_UML.png**

#### Mapping to GoF pattern elements:

| GoF element | Code element |
| ----------- | ------------ |
| Singleton      | DateUtil      |
| uniqueInstance | instance      |
| instance()     | getInstance() |
| Singleton()    | DateUtil()    |

## Task 3 - Full UML Class diagram

### Name of file of full UML Class diagram attached

**YOUR ANSWER**

## Task 4 - Implement new feature

### What pattern fulfils the need for the feature?

**YOUR ANSWER**

### What is its goal and why is it needed here?

**YOUR ANSWER**

### Name of UML Class diagram attached:

**YOUR ANSWER**

### Mapping to GoF pattern elements:

| GoF element | Code element |
| ----------- | ------------ |
|             |              |

## Task 5 - BONUS - Acceptance tests for Task 4

### Feature file (Cucumber Scenarios)

**NAME OF FEATURE FILE**

### Java class implementing the acceptance tests

**NAME OF JAVA FILE**
