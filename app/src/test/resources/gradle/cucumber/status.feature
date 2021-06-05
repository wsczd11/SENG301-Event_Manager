#
# Created on Wed Apr 09 2021
#
# The Unlicense
# This is free and unencumbered software released into the public domain.
#
# Anyone is free to copy, modify, publish, use, compile, sell, or distribute
# this software, either in source code form or as a compiled binary, for any
# purpose, commercial or non-commercial, and by any means.
#
# In jurisdictions that recognize copyright laws, the author or authors of this
# software dedicate any and all copyright interest in the software to the public
# domain. We make this dedication for the benefit of the public at large and to
# the detriment of our heirs and successors. We intend this dedication to be an
# overt act of relinquishment in perpetuity of all present and future rights to
# this software under copyright law.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
# ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
# WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#
# For more information, please refer to <https://unlicense.org>
#

Feature: U4 - Change the status for an event

#  AC.1 When the status of an event I am attending changes, I receive a notification containing the event name and the new status of this event.
#  AC.2 When being notified of a status change, I print a message containing my name, the event name and the new status of the event.
#  AC.3 When receiving the notification, I can remove myself from the participants list of that event.
#  AC.4 If an event is “archived”, all participants are removed from that event.

  Background:
    Given One event with name "SENG301 Lab", description "Let's learn some patterns", type "lab" and date "08/10/2021"
    And participant "Erica" and participant "Tom" has been add to this event

  Scenario: AC1 - I will get notification when the status of an event (which I attending) changed.
    Given The current status is "SCHEDULED"
    When the status change to PAST
    Then Every participants who attend this event will receive a notification contain the name and new status of this event.

  Scenario: AC2 - I will print a message containing my name, the event name and the new status of the event when I get notification for changing status.
    Given The current status is "SCHEDULED"
    When the status change to PAST
    And I receive the notification
    Then A message containing my name, the event name and the new status "PAST" will been print.

  Scenario: AC3 - I can remove myself, when I receive notification
    Given The current status is "SCHEDULED"
    And the status change to PAST
    When I receive the printing message contain new status "PAST", and I want to delete myself from this event, my name is "Erica"
    Then I have been removed in current event, my name is "Erica"

  Scenario: AC4 - When the status of an event has been change to "ARCHIVED"
    Given The current status is "SCHEDULED"
    And the status change to PAST
    When the status change to ARCHIVED
    Then No participant in this event
