package fyi.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Coroutine Dispatcher Android.
 */
actual class Dispatcher {
    actual companion object {
        actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Main
    }
}