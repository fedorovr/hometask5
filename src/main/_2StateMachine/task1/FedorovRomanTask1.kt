package _2StateMachine.task1

import _2StateMachine.model.Command
import _2StateMachine.model.Event
import _2StateMachine.model.State

fun State.commands(vararg commands: Command): State = apply { commands.forEach { addCommand(it) } }

fun State.transition(event: Event, state: State): State = apply { addTransition(event, state) }

fun State.configure(init: State.() -> State): State = init()
