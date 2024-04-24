package com.nautsch.htmxbook.contactmanagement

import kotlinx.coroutines.*
import org.springframework.stereotype.Component

enum class ArchiverStatus {
    WAITING,
    RUNNING,
    COMPLETED
}

@Component
class Archiver {

    private val coroutineContext = Dispatchers.Default
    private var scope = CoroutineScope(coroutineContext + Job())

    private var _status: ArchiverStatus = ArchiverStatus.WAITING
    val status: String
        get() = _status.name

    // represents the progress percentage as a value between 0 and 1
    private var _progress = 0
    val progress: Int
        get() = _progress

    fun start() {
        _status = ArchiverStatus.RUNNING
        scope = CoroutineScope(coroutineContext + Job())

        scope.launch {
            for (i in 1..10) {
                delay(1000L) // simulate one second of work
                _progress = i * 10 // increase progress by 10% each second
            }
            _status = ArchiverStatus.COMPLETED
        }
    }

    fun stop() {
        _status = ArchiverStatus.WAITING
        _progress = 0
        scope.cancel() // stop the asynchronous task
    }
}