package com.demo.communicationexample.domain

/**
 * ConnectionResult is a sealed interface that represents the result of a connection attempt.
 */
sealed interface ConnectionResult {

    /**
     * Represents a successful connection attempt
     */
    data object Connected : ConnectionResult

    /**
     * Represents a message transfer attempt
     */
    data class Transferred(val message: String) : ConnectionResult

    /**
     * Represents an error during the connection attempt
     */
    data class Error(val message: String) : ConnectionResult
}
