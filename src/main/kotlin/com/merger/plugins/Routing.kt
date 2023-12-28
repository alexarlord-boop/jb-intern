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
import kotlinx.coroutines.launch
import kotlinx.css.html
import kotlinx.css.script
import kotlinx.html.*
import kotlinx.html.dom.document
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
                    nav {
                        ol(classes = "breadcrumb") {
                            li(classes = "breadcrumb-item fw-bold") {
                                attributes["ariaCurrent"] = "page"
                                +"Home"
                            }
                            li(classes = "breadcrumb-item active") {
                                +""
                            }
                        }
                    }

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

                            // Button for fetching workflow logs
                            submitInput(classes = "btn btn-primary btn-lg m-5") {
                                value = "Get workflows"
                                // Set the action type value for the second button
                                attributes["onclick"] =
                                    "document.getElementsByName('actionType')[0].value='getWorkflowLogs';"

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

                        nav {
                            ol(classes = "breadcrumb") {
                                li(classes = "breadcrumb-item active") {
                                    a(href = "/", classes = "text-secondary") { +"Home" }
                                }
                                li(classes = "breadcrumb-item fw-bold") {
                                    attributes["ariaCurrent"] = "page"
                                    +"Workflows"
                                }
                            }
                        }


                        div(classes = "d-flex justify-content-between") {
                            h1(classes = "d-inline") {
                                +"Workflows"
                            }
                            div(classes = "d-inline fs-5") {
                                span(classes = "") { +"repo: " }
                                a(
                                    href = "https://github.com/${repositoryOwner}/${repositoryName}",
                                    classes = "text-primary d-inline"
                                ) { +"${repositoryOwner}/${repositoryName}" }
                            }
                        }

                        div(classes = "accordion") {
                            id = "commitAccordion"

                            // Group runs by name
                            val runsByName =
//                                workflowRuns.groupBy { it.name }.toList().sortedByDescending { it.second.size }
                                workflowRuns.groupBy { it.name }.toSortedMap(compareBy { it.lowercase() })


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

                                                                // Add a unique ID for the modal
                                                                val modalId = "jobsModal${workflowRuns.indexOf(run)}"
                                                                val jobsBtnId = "jobsBtn${workflowRuns.indexOf(run)}"



                                                                button(
                                                                    type = ButtonType.button,
                                                                    classes = "btn btn-md btn-dark"

                                                                ) {
                                                                    attributes["id"] = "#$jobsBtnId"
                                                                    attributes["data-bs-toggle"] = "modal"
                                                                    attributes["data-bs-target"] = "#$modalId"
                                                                    +"Jobs"
                                                                }

                                                                p(classes = "d-inline") { +"-- Check jobs status, comprehensive info." }


                                                                // JOBS MODAL START
                                                                div(classes = "modal fade") {
                                                                    id = modalId  // Set the modal ID
                                                                    tabIndex = "-1"
                                                                    role = "dialog"
                                                                    attributes["ariaHidden"] = "true"
                                                                    attributes["jobsUrl"] = run.jobs_url
                                                                    attributes["authToken"] = authToken
                                                                    //attributes["on-bs-show"] = "fetchDataAndPopulateModalBody('$modalId')"

                                                                    div(classes = "modal-dialog modal-lg") {
                                                                        role = "document"
                                                                        div(classes = "modal-content") {
                                                                            // Your modal content goes here
                                                                            div(classes = "modal-header") {
                                                                                h5(classes = "modal-title") { +"${run.name} Jobs" }
                                                                                button(
                                                                                    type = ButtonType.button,
                                                                                    classes = "btn-close"
                                                                                ) {
                                                                                    attributes["data-bs-dismiss"] =
                                                                                        "modal"
                                                                                    attributes["ariaLabel"] = "Close"
                                                                                    // Close button for the modal
                                                                                }
                                                                            }
                                                                            div(classes = "modal-body") {
                                                                                // Content for the modal body

                                                                            }
                                                                            div(classes = "modal-footer") {
                                                                                // Footer buttons or additional content
                                                                                button(
                                                                                    type = ButtonType.button,
                                                                                    classes = "btn btn-secondary",
                                                                                ) {
                                                                                    attributes["data-bs-dismiss"] =
                                                                                        "modal"
                                                                                    +"Close"
                                                                                }
                                                                                // Add more buttons if needed
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                // JOBS MODAL END


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




                        script { src = "https://code.jquery.com/jquery-3.6.0.min.js" }
                        script { src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" }
                        script { src = "/content/functions.js" }

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
