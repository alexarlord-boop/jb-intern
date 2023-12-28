# JetBrains Internship 2024
## Merge Queue for Kotlin Project
### Test Task 2: Git logs checker application
 [test task 1](https://github.com/alexarlord-boop/jb-intern/blob/main/task1.md)

### Description
Develop a client-server application that displays a Git repository's log, retrieved on the server, in a web browser. If required, make use of assumptions to simplify the problem. Any framework and language can be used for the task, but staying in the JVM stack is preferable.

### Assumptions
* GitHub REST API as a log source
* Only public repos assessed
* Workflow runs (github actions) as a log content
* User makes authorised requests to the API
* We have user authN, authZ implemented and have access to github api token for authorized request.

## Solution

**Main screen**

The 1st screen has a text input. User can paste any public repository URL hosted on GitHub and fetch logs specifically for this repo.
![main.gif](videos%2Fmain.gif)

**Workflow screen**

The second screen is showing all workflows of the chosen repository, sorted in alphabetical order.

Each workflow item is an accordion object, that can store additional functionality and be available right away, without redirecting.
![workflows.gif](videos%2Fworkflows.gif)


**Workflow jobs**

Pressing on "Jobs" button, user fetch a workflow run logs with jobs info.
![jobpress.gif](videos%2Fjobpress.gif)

Each job is represented as a tabpane with step list.
I decided to mark a job conclusion right on the tab link for better UX.
![joberror.gif](videos%2Fjoberror.gif)
