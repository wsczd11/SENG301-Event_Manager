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

Feature: U3 - Add participant to event

  Scenario: AC1 - I can add one existing participant to an event by its name (non-empty).
    Given There is one event with name "SENG301 Lab", description "Let's learn some patterns", type "lab" and date "08/06/2021"
    And There is a participant with name "Erica"
    When I add this participant to the given event
    Then The participant "Erica" has been add to event "SENG301 Lab"

  Scenario: AC2 - I can add one participant that does not exist yet. This participant will be created as a side effect with given non-empty name.
    Given There is one event with name "SENG301 Lab", description "Let's learn some patterns", type "lab" and date "08/06/2021"
    And There is no participant with name "Erica"
    When I add a not exist participant "Erica" to the given event
    Then The participant "Erica" has been add to event "SENG301 Lab"

#  AC.3 I cannot add empty participants, or participants with names containing invalid characters (e.g., numbers).
  Scenario: AC3 - The name of participant must not be empty
    Given There is an event with name "SENG301 Asg 3", description "Let's learn some patterns", type "assignment" and date "07/06/2021"
    When I add not exist participants " " and "Erica" to Event "SENG301 Asg 3"
    Then The participant " " has not been add to event "SENG301 Asg 3"
    Then The participant "Erica" has been add to event "SENG301 Asg 3"
