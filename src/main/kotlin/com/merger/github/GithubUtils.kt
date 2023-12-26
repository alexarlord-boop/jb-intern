package com.merger.github

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.*


val customDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
val customPreciseDateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm:ss", Locale.ENGLISH)

fun writeJsonToFile(jsonResponse: String, filePath: String) {
    File(filePath).writeText(jsonResponse)
}

fun getCommits(repositoryOwner: String, repositoryName: String, authToken: String = ""): List<Commit> {
    val apiUrl = "https://api.github.com/repos/$repositoryOwner/$repositoryName/commits"
    val client = OkHttpClient()

    val request = Request.Builder()
        .url(apiUrl)
        .header("Accept", "application/vnd.github.v3+json")
        .header("Authorization", "Bearer $authToken")
        .build()

    val response: Response = client.newCall(request).execute()

    return if (response.isSuccessful) {
        // Successfully fetched the data
        val jsonString = response.body?.string() ?: ""

        val outputFilePath = "${repositoryName.lowercase()}-commits.json"
        writeJsonToFile(jsonString, outputFilePath)
//        parseCommitMessages(jsonString)
        parseCommitMessages(jsonString)
    } else {
        // Handle error
        throw RuntimeException("Failed to fetch Git log. HTTP status code: ${response.code}")
    }
}

fun parseCommitMessages(jsonResponse: String): List<Commit> {

    val gson = Gson()

    val commitsListType = object : TypeToken<List<Commit>>() {}.type
    val commitsList: List<Commit> = gson.fromJson(jsonResponse, commitsListType)

    return commitsList

}

fun getWorkflowRuns(repositoryOwner: String, repositoryName: String, authToken: String = ""): List<WorkflowRun> {
    val apiUrl = "https://api.github.com/repos/$repositoryOwner/$repositoryName/actions/runs"
    val client = OkHttpClient()

    val request = Request.Builder()
        .url(apiUrl)
        .header("Accept", "application/vnd.github.v3+json")
        .header("Authorization", "Bearer $authToken")
        .build()

    val response: Response = client.newCall(request).execute()

    return if (response.isSuccessful) {
        // Successfully fetched the data
        val jsonString = response.body?.string() ?: ""

        val outputFilePath = "${repositoryName.lowercase()}-workflow.json"
        writeJsonToFile(jsonString, outputFilePath)
        parseWorkflowRuns(jsonString).workflow_runs

    } else {
        // Handle error
        throw RuntimeException("Failed to fetch Workflow log. HTTP status code: ${response.code}. \n\n Info: ${response}")
    }
}

fun parseWorkflowRuns(jsonResponse: String): WorkflowRunsResponse {
    val gson = Gson()
    val workflowRunsResponse: WorkflowRunsResponse = gson.fromJson(jsonResponse, WorkflowRunsResponse::class.java)

    return workflowRunsResponse
}

fun getRunJobs(jobsUrl: String, authToken: String = ""): String {

    val client = OkHttpClient()

    val request = Request.Builder()
        .url(jobsUrl)
        .header("Accept", "application/vnd.github.v3+json")
        .header("Authorization", "Bearer $authToken")
        .build()

    val response: Response = client.newCall(request).execute()

    return if (response.isSuccessful) {
        // Successfully fetched the data
        val jsonString = response.body?.string() ?: ""
        jsonString

    } else {
        // Handle error
        throw RuntimeException("Failed to fetch workflow run log. HTTP status code: ${response.code}")
    }
}