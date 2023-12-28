# JetBrains Internship 2024
## Merge Queue for Kotlin Project
### Test Task 1: Improving Kotlin Continuous Integration
 [test task 2](https://github.com/alexarlord-boop/jb-intern/blob/main/README.md)

**Goal:** enhance the reliability and productivity of the Kotlin team's CI process.

**Task:** create an addition to the CI that organizes incoming pushes into a queue, allowing them to proceed only if all quality gates pass.

**Challenge:** make this queue suitable for a large team and compatible with multiple connected repositories.

# Solution Approach
### Understanding the Current CI Workflow
I would familiarize myself with the existing CI setup to understand the current workflow, including how pushes trigger builds, the existing quality gates, and the overall architecture, including official documentation.

### Define Scope
During initial discussion and further collaboration with the team I would investigate and define specific requirements for the CI queue, taking into consideration the needs of a large team, define boundaries and limits of the preferred solution to narrow down the focus.

### Research on Existing Solutions, used Tech Stack
Explore existing CI/CD tools and systems that the team is willing to have in solution, identify potential substitute solutions or components that can be integrated into the Kotlin CI.

### Design and Architecture
I conducted a thorough brainstorming session to devise a comprehensive solution, drawing upon my own insights and considerations.

**Assumptions:**
+ processing is not consecutive & not waiting for each run => asynchronous processing
+ several connected repositories => parallel processing
+ addition to mainstream software & potential growth => system modularity with scalability

**Requirements:**
+ ensure that a push can proceed only if all the required checks and tests pass
+ manage pushes across multiple connected repositories
+ synchronization, dependency management, and ensuring consistency in the overall CI process
+ definition of code quality might vary in repos => flexibility for diverse workflows (predefined quality gates)




**Output:**

Solution can be considered a type of proxy.

<details><summary>System characteristics (expand)</summary>

+ **Intermediary Role --** role as an intermediary layer, emphasizing its function in managing and directing code pushes through the CI process.


+ **Customization and Flexibility --** a customizable layer that can be tailored to the unique requirements of each development team.


+ **Control Point for CI Process --** the system overseeing the flow of pushes, checks, and tests while providing a centralized interface for configuration.


+ **Asynchronous and Parallel Processing --**
The proxy employs asynchronous processing, organizing incoming pushes into a queue and handling them independently for parallel processing.


+ **Quality Gates and Requirements Enforcement --**
The proxy enforces strict requirements, allowing a push to proceed only if all required checks and tests pass. It integrates with quality gates to maintain code quality standards.


+ **Multiple Connected Repositories --**
Serving as a centralized point of control, the proxy efficiently manages pushes across multiple connected repositories, handling synchronization, dependency management, and ensuring consistency.


+ **Flexibility for Diverse Workflows --**
Recognizing that code quality definitions may vary, the proxy offers flexibility for diverse workflows, enabling teams to customize CI pipelines and quality gates.
</details>

<details><summary>Architecture overview (expand)</summary>

**Proxy Service:**
Develop a Kotlin-based proxy service to handle incoming code pushes.

**Task Queue:**
Implement a task queue for asynchronous processing using a distributed system like RabbitMQ or Apache Kafka for scalability and parallelism.

**Logging Module:** Implement a logging module that collects and makes logs accessible from various components in a centralized location

**Resolver Module:**
Create a customizable module for quality gates that enforces checks and tests before allowing code pushes to proceed.

**Configuration module**
Enhance flexibility for customization, metadata, setup configuration etc.

**User Interface:**
GUI to provide an intuitive centralized configuration interface.
</details>

<details><summary>Tech stack (expand)</summary>

**Client-Server:** Ktor, Kotlin

**CI Server:** TeamCity

**Task queue for asynchronous processing:** Apache Kafka, Dockerized build agents

**CI Quality Gates Check Logic:** Jenkins, GitLab CI, Travis CI, etc.

**Integration with Version Control System:** Git, GitHub, GitLab, Bitbucket, etc.

**Optional AWS Integration:** EC2, Lambda, IAM, CloudWatch, etc.
</details>

<details><summary>Example workflow (expand)</summary>

**1. Push Event:**
A developer pushes code changes to the version control system.

**2. Queuing:**
The event is sent to the message queue for asynchronous processing.

**3. Asynchronous Processing:**
Consumers subscribed to the message queue process the event and trigger the CI quality gates check.

**4. CI Quality Gates Check:**
The CI system performs the necessary checks and tests based on the incoming push.

**5. Push resolution**

**6. Result Notification:**
The result of the CI quality gates check is communicated back to relevant stakeholders.
</details>

<details><summary>Diagrams (expand)</summary>

**Current situation**
![noproxy.png](images%2Fnoproxy.png)

**Proxy solution**
![withproxy.png](images%2Fwithproxy.png)

**Several repositories. Many to many**
![manyrepos.png](images%2Fmanyrepos.png)

**Proxy layout**
![proxy.png](images%2Fproxy.png)

**Proxy layout in details**
![details.png](images%2Fdetails.png)

**Resolver (async processing of pushes & checks)**

GQ checker revise results of independent async workers.
![resolve.png](images%2Fresolve.png)



</details>
