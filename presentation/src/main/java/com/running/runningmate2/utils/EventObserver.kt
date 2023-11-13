package com.running.runningmate2.utils

import androidx.lifecycle.Observer

/**
 * Created by pureum on 2022/09/08.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>?> {

    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }

}