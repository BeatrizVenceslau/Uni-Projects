# Turmas

Distributed Systems Project 2021/2022

## Authors

**Group T24**

### Team Members

| Number | Name              | User                             | Email                               |
|--------|-------------------|----------------------------------|-------------------------------------|
| 93734  | Maria Beatriz Venceslau | <https://github.com/beatrizvenceslau>   | <mailto:beatriz.venceslau@tecnico.ulisboa.pt>   |
| 93740  | Miguel Oliveira       | <https://github.com/mardlive>     | <mailto:miguel.d.oliveira@tecnico.ulisboa.pt>     |
| 96159  | André Venceslau     | <https://github.com/avenceslau> | <mailto:andre.venceslau@tecnico.ulisboa.pt> |

## Getting Started

The overall system is made up of several modules. All servers when booted are registered in the naming server which the clients communicate with. The naming server
is the _NamingServer_ class. The class servers are the _ClassServer_ class. The clients are the _Student_,
the _Professor_ and the _Admin_ classes. The definition of messages and services is in the _Contract_. 

See the [Project Statement](https://github.com/tecnico-distsys/Turmas) for a complete domain and system description.

### Prerequisites

The Project is configured with Java 17 (which is only compatible with Maven >= 3.8), but if you want to use Java 11 you
can too, just downgrade the version in the POMs.

To confirm that you have them installed and which versions they are, run in the terminal:

```
javac -version
mvn -version
```

### Installation

To compile and install all modules:

```s
mvn clean install
```

### Execution
If the naming server is not running all the other processes cannot comunicate with each other. 

Be sure to run each different client and server in different terminals.

To run the naming server (The naming server runs on port 5000):

```
mvn exec:java -Dexec.args="nameserver"
```

To run the naming server in debug mode:

```
mvn exec:java -Dexec.args="nameserver -debug"
```

To run the server (`<port>` is any valid port, `<qualifier>` is either P or S):

```
mvn exec:java -Dexec.args="localhost <port> <qualifier>"
```

To run the server in debug mode:

```
mvn exec:java -Dexec.args="localhost <port> <qualifier> -debug"
```

To run the student (with XXXX as four digits):

```
mvn exec:java -Dexec.args="alunoXXXX student_name"
```

To run the student in debug mode:
```
mvn exec:java -Dexec.args="alunoXXXX student_name -debug"
```

To run the admin and the professor the command is the same:

```
mvn exec:java
```

To run the admin and the professor in debug mode:

```
mvn exec:java -Dexec.args="-debug"
```
### Implemented Commands

At this phase in the project the implemented commands are:

For the Student:
* list
* enroll
* exit

For the Professor: (with aluno_Id as the word "aluno" followed by four digits)
* list
* cancelEnrollment <aluno_Id>
* openEnrollments <Capacity_of_the_Class>
* closeEnrollments
* exit

For the Admin: (All Admin commands can receive P and S as arguments, if there are no arguments or there is an invalid argument the command will be executed in the primary server).
* dump
* activate
* deactivate
* deactivateGossip
* activateGossip
* gossip
* exit

## Built With

* [Maven](https://maven.apache.org/) - Build and dependency management tool;
* [gRPC](https://grpc.io/) - RPC framework.
