package fyi.repository.node

/**
 * Class that represents a Node event that will be listened to by the WebSocket class.
 * @property event The event that will be listened to. Ex: device-licensed, device-unlicensed.
 * @property onEvent The lambda that will be executed when the event occurs.
 */
data class NodeEvent private constructor(val event: String, val onEvent: () -> Unit) {

    /**
     * Builder used in the init method of Infinitum to easily add NodeEvents.
     * @property eventList List of the events inserted by the user.
     */
    class NodeEventBuilder{
        private val eventList = mutableListOf<NodeEvent>()

        /**
         * Function that adds a NodeEvent that responds to an [event] by executing [onEvent] lambda.
         * @return The NodeEventBuilder instance.
         */
        fun addEvent(event: String, onEvent: () -> Unit): NodeEventBuilder {
            if (event.isNotBlank()) {
                eventList.add(NodeEvent(event, onEvent))
            }
            return this
        }

        /**
         * @return The list of events added by the User.
         */
        internal fun getEventList(): List<NodeEvent> {
            return eventList
        }
    }
}
