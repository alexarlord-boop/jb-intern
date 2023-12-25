package com.merger.github

import java.util.Date

data class Commit(
    val sha: String,
    val node_id: String,
    val commit: CommitDetails,
    val url: String,
    val html_url: String,
    val comments_url: String,
    val author: User,
    val committer: User,
    val parents: List<ParentCommit>
)

data class CommitDetails(
    val author: AuthorInfo,
    val committer: AuthorInfo,
    val message: String,
    val tree: TreeInfo,
    val url: String,
    val comment_count: Int,
    val verification: VerificationInfo
)

data class AuthorInfo(
    val name: String,
    val email: String,
    val date: Date
)

data class TreeInfo(
    val sha: String,
    val url: String
)

data class VerificationInfo(
    val verified: Boolean,
    val reason: String,
    val signature: Any?,
    val payload: Any?
)

data class ParentCommit(
    val sha: String,
    val url: String,
    val html_url: String
)

data class User(
    val login: String,
    val id: Int,
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