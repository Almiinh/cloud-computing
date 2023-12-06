# Cloud Project Group 9


## Overview


This project is a Java application utilizing the AWS SDK to create a client-server architecture for efficient file processing and data analysis. It integrates with various AWS services such as Amazon S3 and SQS, offering a scalable and robust solution for handling data storage and message queueing.

This work was made by the Group 9 of the cloud computing course.


## Prerequisites


- Java Development Kit (JDK)
- Maven
- AWS SDK for Java


## Setup


### Adding Dependencies


The pom.xml file should handle this when importing the repo. You should use it to install all dependencies and avoid any conflicts.


## Modules


### Client Application (`ClientApp.java`)
Handles file uploading to S3 buckets and message sending to SQS queues.


### SQS Message Handling
- `SQSSendMessage`: Manages sending messages to SQS.
- `SQSRetrieveMessage`: Retrieves messages from SQS.
- `SQSDeleteMessageClient`: Deletes messages from SQS.
- `SQSCreateQueue`: Creates new SQS queues.


### EC2 Worker (`EC2Worker`)
Processes files from S3 based on SQS messages.


### CSV Parser (`CSVParser`)
Manages parsing and analyzing CSV files.

### Transaction Processors
- `TransactionAggregate`: Aggregate information from the CSV file to compute statistics.
- `Transcation`: Adapt CSV and data format before data processing and computation.

### S3 Controllers
- `S3ControllerCreate`: Creates S3 buckets.
- `S3ControllerGetObject`: Retrieves objects from S3.
- `S3ControllerPutObject`: Uploads objects to S3.
- `S3ControllerAnalyseData`: Performs data analysis on CSV data.


### Data Models
- `Transaction`: Models a single transaction record.
- `TransactionAggregate`: Aggregates transactions for analysis.


## Solution Architecture


### System Interaction
- The **Client Application** initiates the process by uploading files to **Amazon S3** and sending messages to **Amazon SQS**.
- The **EC2 Worker** listens to SQS messages, processes files from S3, and communicates results back through SQS.


### Data Flow
- Data flow starts from local file upload to S3, followed by message queueing in SQS, processing in EC2 or Lambda, and finally results storage in S3.


## AWS Services Used


- **Amazon S3**: Used for storing and retrieving data files.
- **Amazon SQS**: Manages message queues for coordinating between different application components.
- **Amazon EC2**: Hosts the Java application worker for processing data.
- **AWS Lambda**: Optionally used for serverless computing.


## Justification of Architecture and AWS Services


- The use of **S3** ensures reliable and scalable storage.
- **SQS** offers a robust system for message queuing and decoupling components.
- **EC2** provides a flexible environment for running complex Java applications.
- **Lambda** offers a serverless option, reducing operational overhead.


## UML Diagram
[Include UML Diagram here]


## Comparison Between Lambda and Java Application Worker


### Performance
- Lambda offers quick scalability whereas EC2 provides consistent performance.


### Cost
- Lambda has a pay-per-use model, beneficial for sporadic workloads, while EC2 incurs costs based on instance uptime.


### Scalability
- Lambda scales automatically, while EC2 requires manual scaling.


### Maintainability
- Lambda functions are easier to deploy and manage compared to managing EC2 instances.


## Running the Application


1. Install all prerequisites.
2. Configure AWS credentials.
3. Compile the project with Maven.
4. Execute `ClientApp.java` to start.


## Troubleshooting


- Common issues include AWS service misconfigurations, network issues, or dependency conflicts.


## Contact Information


- Minh-Hoang Huynh: minh-hoang.huynh@etu.emse.fr
- Ninon Lahiani: ext.21m2017@etu.emse.fr
- Julien Séailles: julien.seailles@etu.emse.fr
