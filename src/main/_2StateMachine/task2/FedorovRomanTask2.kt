package _2StateMachine.task2

import _2StateMachine.model.Command
import _2StateMachine.model.Event
import _2StateMachine.model.State
import _2StateMachine.model.StateMachine

fun stateMachine(start: String, init: StateMachineBuilder.() -> Unit): StateMachine {
    return StateMachineBuilder(State(start)).apply { init() }.getMachine()
}

class StateMachineBuilder(val start: State) {
    val processedStates: MutableList<StateBuilder> = mutableListOf<StateBuilder>()
    var futureStates: MutableList<StateBuilder> = mutableListOf<StateBuilder>()

    private val events = mutableListOf<Event>()
    private val resetEvs = mutableListOf<Event>()
    private val commands = mutableListOf<Command>()

    init {
        futureStates.add(StateBuilder(start))
    }

    fun command(commandName: String) =
            if (commands.any { it.code.equals(commandName) })
                throw IllegalStateException()
            else
                commands.add(Command(commandName))

    fun event(eventName: String) =
            if (events.any { it.code.equals(eventName) })
                throw IllegalStateException()
            else
                events.add(Event(eventName))

    fun resetEvents(vararg resetEvents: String) {
        resetEvents.forEach {
            val eventIdx = events.indexOfFirst { e -> e.code.equals(it) }
            if (eventIdx == -1)
                throw IllegalStateException()
            else
                resetEvs.add(events[eventIdx])
        }
    }

    fun state(stateName: String, init: StateBuilder.() -> Unit) {
        if (processedStates.any { it.state.code.equals(stateName) })
            throw IllegalStateException()
        val stateIdx = futureStates.indexOfFirst { it.state.code.equals(stateName) }
        val newState = if (stateIdx == -1) StateBuilder(State(stateName)) else futureStates[stateIdx]
        if (stateIdx != -1) futureStates = futureStates.filterNot { it.state.code.equals(stateName) }.toMutableList()
        processedStates.add(newState)
        newState.init()
    }

    fun getMachine(): StateMachine {
        if (futureStates.isNotEmpty())
            throw IllegalStateException()
        return StateMachine(start).apply { resetEvs.forEach { addResetEvent(it) } }
    }

    inner class StateBuilder(val state: State) {
        fun transition(event: String, target: String) {
            if (events.any { it.code.equals(event) }) {
                val targetState = if (processedStates.any { it.state.code.equals(target) }) {
                    processedStates.first { it.state.code.equals(target) }.state
                } else if (futureStates.any { it.state.code.equals(target) }) {
                    futureStates.first { it.state.code.equals(target) }.state
                } else {
                    State(target).apply { futureStates.add(StateBuilder(this)) }
                }
                state.addTransition(events.first { it.code.equals(event) }, targetState)
            } else {
                throw IllegalStateException()
            }
        }

        fun commands(vararg commands: String) {
            commands.forEach {
                val commandIdx = this@StateMachineBuilder.commands.indexOfFirst { c -> c.code.equals(it) }
                if (commandIdx == -1)
                    throw IllegalStateException()
                else
                    state.addCommand(this@StateMachineBuilder.commands[commandIdx])
            }
        }
    }
}
