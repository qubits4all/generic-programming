package info.willdspann.generic.state

import info.willdspann.generic.MultiTypeMap

import scala.reflect.runtime.universe.{TypeTag, typeOf, typeTag}

class MultiTypeState[K] private() {
    private val stateMap = MultiTypeMap.empty[K]
    private var currentState: Option[KeyedState[K, _]] = None

    def current[T : TypeTag]: Option[T] = {
        currentState match {
            case Some(KeyedState(_, value, ttag)) =>
                if (typeOf[T] <:< ttag.tpe) {
                    Some(value.asInstanceOf[T])
                } else {
                    None
                }
            case None => None
        }
    }

    def currentKey: Option[K] = {
        currentState match {
            case Some(KeyedState(key, _, _)) =>
                Some(key)
            case None => None
        }
    }

    def put[T : TypeTag](key: K, state: T): Boolean = {
        stateMap.put(key, state)
    }

    def get[T : TypeTag](key: K): Option[T] = {
        stateMap.get[T](key)
    }

    def putAndSwitch[T : TypeTag](key: K, state: T): T = {
        stateMap.put(key, state)
        currentState = Some(KeyedState(key, state, typeTag[T]))
        state
    }

    def switch[T : TypeTag](key: K): Option[T] = {
        stateMap.get[T](key).map { state =>
            currentState = Some(KeyedState(key, state, typeTag[T]))
            state
        }
    }
}

object MultiTypeState {

    def apply[K, T : TypeTag](key: K, initialState: T): MultiTypeState[K] = {
        val state = new MultiTypeState[K]()
        state.put(key, initialState)
        state.currentState = Some(KeyedState(key, initialState, typeTag[T]))
        state
    }
}
