
# Enron Email Analysis

## Overview

This project is part of the CS 245 course assignment titled "Friends in a Scandal". The goal of this assignment is to demonstrate mastery of graphs, graph algorithms, and object-oriented design by efficiently handling a large dataset of Enron emails.

## Functional Requirements

### Requirement 1: Read the Data File

The program reads valid mail files from the provided Enron dataset. The path to the dataset is given as the first argument to the program. The dataset can be obtained from [this link](https://www.cs.cmu.edu/~./enron/enron_mail_20150507.tar.gz). Uncompress the dataset using:
```
tar -xvzf enron_mail_20150507.tar.gz
```

### Requirement 2: Identify and Print Connectors

The program identifies connectors in the friendship graph constructed from the dataset. Connectors are vertices in the graph which, if removed, would increase the number of connected components. The identified connectors are printed to `stdout` and optionally to a file provided as the second argument.

### Requirement 3: Provide Details of Each Person

The program can respond to user queries about individual email addresses, providing:
- The number of unique email addresses to whom the individual sent messages
- The number of unique email addresses from whom the individual received messages
- The number of email addresses in the same "team" as the individual

## Files

### A3.java

This file contains the main implementation of the assignment. The key functionalities include:
- Reading the Enron email dataset
- Constructing a friendship graph
- Identifying and printing connectors
- Responding to user queries about individual email addresses

## Running the Program

To run the program, use the following command:
```
java A3 /path/to/enron/maildir /path/to/output/connectors.txt
```
The second argument is optional. If not provided, the connectors will only be printed to `stdout`.

### Example Interaction

```
Email address of the individual (or EXIT to quit): kate.symes@enron.com
* kate.symes@enron.com has sent messages to X others
* kate.symes@enron.com has received messages from X others
* kate.symes@enron.com is in a team with X individuals
Email address of the individual (or EXIT to quit): notme@usfca.edu
Email address (notme@usfca.edu) not found in the dataset.
Email address of the individual (or EXIT to quit): EXIT
```
## Warning

For academic honesty, do not replicate or use this code for coursework or assessments.
