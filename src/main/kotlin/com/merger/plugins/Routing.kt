package com.merger.plugins

import com.merger.github.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.html
import kotlinx.css.script
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import java.text.SimpleDateFormat
import java.util.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }

    routing {

        staticResources(
            "/content",
            "mycontent"
        )

        get("/test") {
            val text = "<h1>Hello From Ktor</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }

        get("/error-test") {
            throw IllegalStateException("Too Busy")
        }



        get("/") {
            call.respondHtml {
                head {
                    link(rel = "stylesheet", href = "/assets/bootstrap/bootstrap.css")
                    title("Introduction Page")
                    style {
                        +"""
                            body {
                                font-family: 'JetBrains Sans', sans-serif, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Oxygen', 'Ubuntu', 'Cantarell', 'Droid Sans', 'Helvetica Neue', 'Arial', sans-serif;
                            }
                        """.trimIndent()
                    }
                }

                body(classes = "container my-2") {
                    h1 {
                        +"Merge Queue for Kotlin Project"
                    }
                    h3 {
                        +"Test task solution"
                    }
                    p(classes = "col-8") {
                        +"Develop a client-server application that displays a Git repository's log, retrieved on the server, in a web browser. If required, make use of assumptions to simplify the problem. Any framework and language can be used for the task, but staying in the JVM stack is preferable."
                    }


                    br()
                    br()
                    br()
                    div(classes = "container my-5 text-center text-primary") {
                        form(action = "/process-input", method = FormMethod.post, classes = "w-75 mx-auto") {

                            label(classes = "w-100") {
                                p(classes = "text-start m-0  fs-6") { +"Input a link to any public repo:" }
                                textInput(name = "repository", classes = "form-control fs-4 ") {
                                    required = true
                                    value = "https://github.com/simplesamlphp/simplesamlphp"
                                    placeholder = "Public repository url:"
                                }
                            }

                            // Add a hidden input field to store the action type
                            hiddenInput(name = "actionType") { value = "" }

                            br()

                            // Button for fetching repo commits
//                            submitInput(classes = "btn btn-dark btn-lg m-5") {
//                                value = "Get repo commits"
//                                // Set the action type value for the first button
//                                attributes["onclick"] =
//                                    "document.getElementsByName('actionType')[0].value='getCommits';"
//                            }

                            // Button for fetching workflow logs
                            submitInput(classes = "btn btn-primary btn-lg m-5") {
                                value = "Get workflow logs"
                                // Set the action type value for the second button
                                attributes["onclick"] =
                                    "document.getElementsByName('actionType')[0].value='getWorkflowLogs';"

//                                attributes["data-bs-toggle"]="tooltip"
//                                attributes["data-bs-placement"]="bottom"
//                                attributes["title"]="Tooltip on bottom"


                            }
                        }
                    }


                }
            }
        }


        // Route to process user input and redirect to another URL
        post("/process-input") {
            val parameters = call.receiveParameters()

            val repo = parameters["repository"]!!
            val (owner, name) = repo.split(".com/")[1].split("/")

            val actionType = parameters["actionType"]

            when (actionType) {
                "getCommits" -> {
                    // Handle fetching Git logs
                    // Redirect to another URL or perform the required action
                    call.respondRedirect("/github-repo-commits/$owner/$name")
                }

                "getWorkflowLogs" -> {
                    // Handle fetching workflow logs
                    // Redirect to another URL or perform the required action
                    call.respondRedirect("/github-repo-workflow/$owner/$name")
                }

                else -> {
                    // Handle the default action (if any)
                }
            }

        }


        // COMMITS FETCH START
//        get("/github-repo-commits/{owner}/{name}") {
//            val repositoryOwner = call.parameters["owner"] ?: throw IllegalStateException("Owner parameter is missing.")
//            val repositoryName = call.parameters["name"] ?: throw IllegalStateException("Name parameter is missing.")
//
//
//            val commits = getCommits(repositoryOwner, repositoryName, authToken)
//
//            val text = createHTML().html {
//                head {
//                    link(rel = "stylesheet", href = "/assets/bootstrap/bootstrap.css")
//                    title("Git Repository Log")
//                    style {
//                        +"""
//                        body {
//                            font-family: 'JetBrains Sans', sans-serif, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Oxygen', 'Ubuntu', 'Cantarell', 'Droid Sans', 'Helvetica Neue', 'Arial', sans-serif;
//                        }
//                    """.trimIndent()
//                    }
//                }
//
//                body(classes = "container my-2") {
//                    h1 {
//                        +"Git Repository Log: ${repositoryOwner}/${repositoryName}"
//                    }
//
//
//
//                    div(classes = "accordion") {
//                        id = "commitAccordion"
//
//                        // Group commits by date
//                        val commitsByDate = commits.groupBy { customDateFormat.format(it.commit.author.date) }
//
//                        // Iterate through grouped commits and render HTML
//                        commitsByDate.forEach { (date, commitsForDate) ->
//                            div(classes = "date-section fs-5 mt-3 ") {
//                                span(classes = "ms-3 ") {
//                                    // Format the date as needed
//                                    +date
//                                }
//
//                                commitsForDate.forEach { commit ->
//                                    div(classes = "accordion-item border-1 border-primary") {
//                                        h2(classes = "accordion-header") {
//                                            id = "heading${commits.indexOf(commit)}"
//                                            button(classes = "accordion-button bg-light") {
//                                                attributes["data-bs-toggle"] = "collapse"
//                                                attributes["data-bs-target"] = "#collapse${commits.indexOf(commit)}"
//                                                attributes["aria-expanded"] = "false"
//                                                attributes["aria-controls"] = "collapse${commits.indexOf(commit)}"
//                                                +commit.commit.message
//                                            }
//                                        }
//                                        div(classes = "accordion-collapse collapse") {
//                                            id = "collapse${commits.indexOf(commit)}"
//                                            attributes["aria-labelledby"] = "heading${commits.indexOf(commit)}"
//                                            attributes["data-bs-parent"] = "#commitAccordion"
//                                            div(classes = "accordion-body") {
//                                                p(classes = "fs-6") {
//                                                    +commit.toString()
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//
//
//                    script {
//                        src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
//
//                        unsafe {
//                            raw(
//                                """
//                        function toggleVisibility (index) {
//                            var element = document.getElementById('commitMessage' + index);
//                            element.classList.toggle('hidden');
//                        }
//                        """.trimIndent()
//                            )
//                        }
//                    }
//                }
//
//
//            }
//
//            val type = ContentType.Text.Html
//            call.respondText(text, type)
//
//        }
        // COMMITS FETCH END



        get("/github-repo-workflow/{owner}/{name}") {
            val repositoryOwner = call.parameters["owner"] ?: throw IllegalStateException("Owner parameter is missing.")
            val repositoryName = call.parameters["name"] ?: throw IllegalStateException("Name parameter is missing.")


            var text = ""
            try {
                val workflowRuns = getWorkflowRuns(repositoryOwner, repositoryName, authToken)
                text = createHTML().html {
                    head {
                        link(rel = "stylesheet", href = "/assets/bootstrap/bootstrap.css")
                        link(
                            rel = "stylesheet",
                            href = "https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css"
                        )
                        title("Git Repository Log")
                        style {
                            +"""
                        body {
                            font-family: 'JetBrains Sans', sans-serif, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Oxygen', 'Ubuntu', 'Cantarell', 'Droid Sans', 'Helvetica Neue', 'Arial', sans-serif;
                        }
                    """.trimIndent()
                        }
                    }

                    body(classes = "container my-2") {
                        div (classes = "d-flex justify-content-between") {
                            h1(classes = "d-inline") {
                                +"Workflow logs"
                            }
                            div (classes = "d-inline fs-5") {
                                span (classes = "") {+"repo: "}
                                a(href = "https://github.com/${repositoryOwner}/${repositoryName}", classes = "text-primary d-inline"){+"${repositoryOwner}/${repositoryName}"}
                            }
                            }
                        div(classes = "accordion") {
                            id = "commitAccordion"

                            // Group runs by name
                            val runsByName =
                                workflowRuns.groupBy { it.name }.toList().sortedByDescending { it.second.size }

                            div(classes = "row") {
                                // Iterate through grouped commits and render HTML
                                runsByName.forEach { (name, runsForDate) ->

                                    div(classes = "date-section fs-5 mt-3 flex col-md-6 mb-4") {
                                        style = "max-height: 500px;"
                                        span(classes = "ms-3 d-inline-block") { +name }
                                        div {
                                            style = "max-height: 400px; overflow-y: scroll;"
                                            runsForDate.forEach { run ->
                                                div(classes = "accordion-item border-1 border-primary") {
                                                    h2(classes = "accordion-header") {
                                                        id = "heading${workflowRuns.indexOf(run)}"
                                                        button(classes = "accordion-button collapsed bg-light") {
                                                            attributes["data-bs-toggle"] = "collapse"
                                                            attributes["data-bs-target"] =
                                                                "#collapse${workflowRuns.indexOf(run)}"
                                                            attributes["aria-expanded"] = "true"
                                                            attributes["aria-controls"] =
                                                                "collapse${workflowRuns.indexOf(run)}"

                                                            i(
                                                                classes = "d-inline bi me-3 fs-5 ${
                                                                    if (run.conclusion == "failure") "bi-x-circle-fill text-danger" else "bi-" +
                                                                            "patch-check-fill text-success"
                                                                }"
                                                            ) {}
                                                            div(classes = "d-inline flex-col flex-wrap") {
                                                                +run.head_commit.message
                                                                p(classes = "m-0 text-secondary fs-6") {
                                                                    +customPreciseDateFormat.format(
                                                                        run.created_at
                                                                    )
                                                                }
                                                            }

                                                        }
                                                    }
                                                    div(classes = "accordion-collapse collapse") {
                                                        id = "collapse${workflowRuns.indexOf(run)}"
                                                        attributes["aria-labelledby"] =
                                                            "heading${workflowRuns.indexOf(run)}"
                                                        attributes["data-bs-parent"] = "#commitAccordion"
                                                        div(classes = "accordion-body") {
                                                            div(classes = "fs-6 ") {

                                                                label {
                                                                    a(
                                                                        href = run.logs_url,
                                                                        classes = "d-inline btn btn-md btn-dark"
                                                                    ) { +"Logs" }
                                                                    p(classes = "d-inline") { +"-- Assume a user has admin access to the repo - workflow logs preview will be available" }
                                                                }
                                                                br()
                                                                br()
//                                                                label {
//                                                                    a(
//                                                                        href = run.jobs_url,
//                                                                        classes = " btn btn-md btn-dark"
//                                                                    ) { +"Jobs" }
//                                                                    p(classes = "d-inline") { +"-- Assume jobs logs preview is available" }
//                                                                }

                                                                // Add a unique ID for the modal
                                                                val modalId = "jobsModal${workflowRuns.indexOf(run)}"

// Inside your label element
                                                                button(
                                                                    type = ButtonType.button,
                                                                    classes = "btn btn-md btn-dark"
                                                                ) {
                                                                    attributes["data-bs-toggle"] = "modal"
                                                                    attributes["data-bs-target"] = "#$modalId"
                                                                    +"Jobs"
                                                                }

                                                                p(classes = "d-inline") { +"-- Assume jobs logs preview is available" }


                                                                div(classes = "modal fade") {
                                                                    id = modalId  // Set the modal ID
                                                                    tabIndex = "-1"
                                                                    role = "dialog"
                                                                    attributes["ariaHidden"] = "true"
                                                                    //attributes["on-bs-show"] = "fetchDataAndPopulateModalBody('$modalId')"

                                                                    div(classes = "modal-dialog modal-lg") {
                                                                        role = "document"
                                                                        div(classes = "modal-content") {
                                                                            // Your modal content goes here
                                                                            div(classes = "modal-header") {
                                                                                h5(classes = "modal-title") { +"Jobs Modal" }
                                                                                button(type = ButtonType.button, classes = "btn-close") {
                                                                                    attributes["data-bs-dismiss"] = "modal"
                                                                                    attributes["ariaLabel"] = "Close"
                                                                                    // Close button for the modal
                                                                                }
                                                                            }
                                                                            div(classes = "modal-body") {
                                                                                // Content for the modal body
                                                                                p { +"This is where your jobs logs preview goes." }
                                                                            }
                                                                            div(classes = "modal-footer") {
                                                                                // Footer buttons or additional content
                                                                                button(type = ButtonType.button, classes = "btn btn-secondary", ) {
                                                                                    attributes["data-bs-dismiss"] = "modal"
                                                                                    +"Close" }
                                                                                // Add more buttons if needed
                                                                            }
                                                                        }
                                                                    }
                                                                }


                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }



                        script { src = "https://code.jquery.com/jquery-3.6.0.min.js"}
                        script { src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" }
                        script { src = "/content/functions.js" }
                        script {
                            unsafe {
                                raw(
                                    """
                                    function toggleVisibility (index) {
                                        var element = document.getElementById('commitMessage' + index);
                                        element.classList.toggle('hidden');
                                    }
                                    
                                 
                                    
                                    
                                    function fetchDataAndPopulateModalBody(modalId) {
                                        // Make your asynchronous request here (using fetch, XMLHttpRequest, etc.)
                                        // Example using fetch:
                                        
                                        console.log("bebraaa");
                                    }
                                    """.trimIndent()
                                )
                            }
                        }
                    }


                }
            } catch (e: RuntimeException) {
                text = e.message.toString()
            }

            val type = ContentType.Text.Html
            call.respondText(text, type)

        }
    }
}
