package com.demo.communicationexample.domain

/**
 * ConnectionResult is a sealed interface that represents the result of a connection attempt.
 */
sealed interface ConnectionResult {
    data object Connected : ConnectionResult
    data class Transferred(val message: String) : ConnectionResult
    data class Error(val message: String) : ConnectionResult
}
