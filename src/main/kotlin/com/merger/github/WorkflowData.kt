package com.merger.github

import java.util.*

data class Actor(
    val login: String,
    val id: Long,
    val node_id: String,
    val avatar_url: String,
    val gravatar_id: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val starred_url: String,
    val subscriptions_url: String,
    val organizations_url: String,
    val repos_url: String,
    val events_url: String,
    val received_events_url: String,
    val type: String,
    val site_admin: Boolean
)

data class HeadCommitAuthor(
    val name: String,
    val email: String
)

data class HeadCommit(
    val id: String,
    val tree_id: String,
    val message: String,
    val timestamp: String,
    val author: HeadCommitAuthor,
    val committer: HeadCommitAuthor
)

data class RepositoryOwner(
    val login: String,
    val id: Long,
    val node_id: String,
    val avatar_url: String,
    val gravatar_id: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val starred_url: String,
    val subscriptions_url: String,
    val organizations_url: String,
    val repos_url: String,
    val events_url: String,
    val received_events_url: String,
    val type: String,
    val site_admin: Boolean
)

data class Repository(
    val id: Long,
    val node_id: String,
    val name: String,
    val full_name: String,
    val private: Boolean,
    val owner: RepositoryOwner,
    val html_url: String,
    val description: String,
    val fork: Boolean,
    val url: String,
    val forks_url: String,
    val keys_url: String,
    val collaborators_url: String,
    val teams_url: String,
    val hooks_url: String,
    val issue_events_url: String,
    val events_url: String,
    val assignees_url: String,
    val branches_url: String,
    val tags_url: String,
    val blobs_url: String,
    val git_tags_url: String,
    val git_refs_url: String,
    val trees_url: String,
    val statuses_url: String,
    val languages_url: String,
    val stargazers_url: String,
    val contributors_url: String,
    val subscribers_url: String,
    val subscription_url: String,
    val commits_url: String,
    val git_commits_url: String,
    val comments_url: String,
    val issue_comment_url: String,
    val contents_url: String,
    val compare_url: String,
    val merges_url: String,
    val archive_url: String,
    val downloads_url: String,
    val issues_url: String,
    val pulls_url: String,
    val milestones_url: String,
    val notifications_url: String,
    val labels_url: String,
    val releases_url: String,
    val deployments_url: String
)

data class WorkflowRun(
    val id: Long,
    val name: String,
    val node_id: String,
    val head_branch: String,
    val head_sha: String,
    val path: String,
    val display_title: String,
    val run_number: Int,
    val event: String,
    val status: String,
    val conclusion: String,
    val workflow_id: Long,
    val check_suite_id: Long,
    val check_suite_node_id: String,
    val url: String,
    val html_url: String,
    val pull_requests: List<Any>,
    val created_at: Date,
    val updated_at: Date,
    val actor: Actor,
    val run_attempt: Int,
    val referenced_workflows: List<Any>,
    val run_started_at: Date,
    val triggering_actor: Actor,
    val jobs_url: String,
    val logs_url: String,
    val check_suite_url: String,
    val artifacts_url: String,
    val cancel_url: String,
    val rerun_url: String,
    val previous_attempt_url: Any,
    val workflow_url: String,
    val head_commit: HeadCommit,
    val repository: Repository,
    val head_repository: Repository
)

data class WorkflowRunsResponse(
    val total_count: Int,
    val workflow_runs: List<WorkflowRun>
)