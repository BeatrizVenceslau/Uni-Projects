# Report

We will refer as actions to all commands given by professors or students.

In all Examples A is a student.

---

## Data Structures

Each student now has a timestamp that registers the time when an action was applied to that student.

We created an EnrollTimes/TimeFrame data structure that has a start and end time and the capacity of the class.

---

## Number of Servers

Our project supports an "unlimited" amount of secondary servers.

All servers support all actions, except for open and close enrollments which can be done exclusively on primary servers.

To distribute all actions between all servers, clients access a random server. If by any chance that server is inactive or unresponsive, another server is contacted until one replies or all are tested.

---

## Conflict Resolution

When a server propagates its state, two things are sent, the class state and the history of timeframes where enrolling was possible.

We consider that the most recent action done by a student, or a professor is the correct action in all cases with one exception, when an action is repeated.

Examples:

1. If A enrolls twice in different servers, the one that is considered is the first action.
2. Assuming two servers that have no communication between each other. If A enrolls on server 1, and afterwards enrolls on server 2, and then its enrollment is then cancelled on server 1, after propagations the student will be considered discarded, since it was last action applied on A.

To achieve this, when a server receives a propagation the first thing that is checked is if there are new enroll timeframes in this propagation. If there are, then we have to check if the local enrolls were done in a valid timeframe, if they were than they can stay as is, if they were not, they are discarded.

Examples:

3. Assuming two servers, a Primary (P) and a Secondary (S). If enrollments were opened on P and after state was propagated to S, the connection between servers was lost. If enrollments were closed on P, and after there was enrollment on S, after connection is restored the enrollment is discarded.

After that we proceed to merge the enroll lists.
To do so we check to see if the students in the received state have a timestamp higher than the same student in the local state, if they have and the action applied to them was different, we adjust their timestamp, and try to apply the same action that the sending server applied.

In case of an enroll we check if the student was in an enrollable timeframe, if it was, we try to enroll them.

If a class is full when we try to enroll the student, the student is only enrolled if there is at least one student with timestamp higher than them. In that case we discard the student with the highest timestamp and enroll the received student. This discard counts as an action.

If at any point a check to see if a student can be enrolled fails that student is discarded.
