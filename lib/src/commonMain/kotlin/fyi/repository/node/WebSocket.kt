package fyi.repository.node

import fyi.repository.Repository

/**
 * Class that manages the web socket connection.
 * TODO Implementation on the iOS side. Since there are no multiplatform libraries for websockets that support iOS, this feature is only available for Android.
 */
expect internal class WebSocket(nodeEventBuilder: NodeEvent.NodeEventBuilder, mRepository: Repository){
    internal fun disconnect()
}

