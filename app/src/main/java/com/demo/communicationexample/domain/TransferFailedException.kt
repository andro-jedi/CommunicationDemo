package com.demo.communicationexample.domain

import java.io.IOException

/**
 * Exception thrown when a transfer failed.
 */
class TransferFailedException : IOException("Reading incoming data failed")
