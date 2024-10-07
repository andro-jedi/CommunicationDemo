package com.demo.communicationexample.domain.entities

data class Message(
    val content: String,
    val timestamp: Long,
    val isSent: Boolean
)
