package fyi.repository.node

import fyi.repository.Repository

/**
 * TODO WebSocket for iOS phones. There are no multiplatform libraries that support iOS.
 */
actual internal class WebSocket actual constructor(nodeEventBuilder: NodeEvent.NodeEventBuilder, mRepository: Repository) {
    actual fun disconnect() {
    }
}