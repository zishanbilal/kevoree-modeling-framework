package org.jetbrains.annotations;

import org.kevoree.modeling.api.aspect
import org.fsmsample.Action
import org.fsmsample.State
import org.fsmsample.Transition
import org.fsmsample.FSM

/*
  Created with IntelliJ IDEA.
 * User: duke
 * Date: 21/08/13
 * Time: 10:03
 */

public aspect trait MyAspect : Action {

    override fun run(p: Boolean): String {
        return "";
    }

}

public aspect trait MyStateAspect : FSM{
    //var currentState: State
    // Operational semantic
    fun run() {
        var currentState: State? = null
        // reset if there is no current state
        if (currentState == null) {
            currentState = this.getInitialState()!!
        }
        var str = "init"
        while (str != "quit") {
            java.lang.System.console()?.printf("Current state : " + currentState!!.getName())
            str = System.console()?.readLine("give me a letter : ")!!
            if (str == "quit") {
                System.console()?.printf("")
                System.console()?.printf("quitting ...")
            } else if (str == "print") {
                System.console()?.printf("")
            } else
                System.console()?.printf(str)
            System.console()?.printf("stepping...")
            try {
                var textRes = (currentState as StateAspect).step(str);
                if (textRes == null || textRes == ""){
                    textRes = "NC";
                }
                System.console()?.printf("string produced : " + textRes)
            } catch (
                    err: NonDeterminism) {
                System.console()?.printf(err.toString())
                str = "quit"
            } catch (err: NoTransition) {
                System.console()?.printf(err.toString())
                str = "quit"
            }
        }
    }

}

public aspect trait StateAspect : State { // Go to the next state
    public fun step(c: String): String {

        // Get the valid transitions
        var validTransitions = this.getOutgoingTransition().filter{ t -> t.getInput().equals(c) }
        // Check if there is one and only one valid transition
        if(validTransitions.empty) throw NoTransition()
        if(validTransitions.size > 1) throw  NonDeterminism()

        // Fire the transition
        return (validTransitions.get(0) as TransitionAspect ).fire()
    }
}

public trait TransitionAspect : Transition {
    // Fire the transition
    public fun fire(): String {
        // update FSM current state
        this.getSource()?.getOwningFSM()?.setCurrentState(this.getTarget())
        return this.getOutput()
    }
}

public open class FSMException() : Exception() {
}

class NonDeterminism() : FSMException() {
}

class NoTransition() : FSMException() {
}

class NoInitialStateException() : FSMException() {
}