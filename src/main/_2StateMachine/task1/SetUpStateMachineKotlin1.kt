package _2StateMachine.task1

import _2StateMachine.model.*

fun setUpStateMachine(): StateMachine {
    val doorClosed = Event(DOOR_CLOSED)
    val drawerOpened = Event(DRAWER_OPENED)
    val lightOn = Event(LIGHT_ON)
    val doorOpened = Event(DOOR_OPENED)
    val panelClosed = Event(PANEL_CLOSED)

    val unlockPanel = Command(UNLOCK_PANEL)
    val lockPanel = Command(LOCK_PANEL)
    val lockDoor = Command(LOCK_DOOR)
    val unlockDoor = Command(UNLOCK_DOOR)

    val idle = State(IDLE)
    val active = State(ACTIVE)
    val waitingForLight = State(WAITING_FOR_LIGHT)
    val waitingForDrawer = State(WAITING_FOR_DRAWER)
    val unlockedPanel = State(UNLOCKED_PANEL)

    val machine = StateMachine(idle)

    // Support the following way to configure state machine.
    // Use (and implement) the interface StateBuilder defined in the file FedorovRomanTask1.kt

    idle.configure {
        commands(unlockDoor, lockPanel)
        transition(doorClosed, active)
    }
    active.configure {
        transition(drawerOpened, waitingForLight)
        transition(lightOn, waitingForDrawer)
    }
    waitingForLight.configure {
        transition(lightOn, unlockedPanel)
    }
    waitingForDrawer.configure {
        transition(drawerOpened, unlockedPanel)
    }
    unlockedPanel.configure {
        commands(unlockPanel, lockDoor)

        // Bonus. Make the following way a legal way to define a transition as well.
        // Before implementing it, you can uncomment the 'transition' line below instead.
        // panelClosed leadsTo idle // TODO
        transition(panelClosed, idle)
    }

    machine.addResetEvent(doorOpened)
    return machine
}
