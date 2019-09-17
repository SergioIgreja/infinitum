package fyi.utils

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Helper class to make Coroutines work on both platforms.
 * @property ApplicationDispatcher The dispatcher. Will be different on both platforms.
 */
expect class Dispatcher {
    companion object {
        val ApplicationDispatcher: CoroutineDispatcher
    }
}
