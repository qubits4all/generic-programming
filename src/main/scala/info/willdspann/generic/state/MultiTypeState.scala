package info.willdspann.generic.state

import info.willdspann.generic.MultiTypeMap

import scala.reflect.runtime.universe.{TypeTag, typeOf, typeTag}

class MultiTypeState[K] private() {
    private val stateMap = MultiTypeMap.empty[K]
    private var currentState: Option[(K, TypeTag[_])] = _

    def current[T : TypeTag]: Option[T] = {
        currentState match {
            case Some((key: K, ttag: TypeTag[_])) =>
                if (typeOf[T] <:< ttag.tpe) {
                    stateMap.get[T](key)
                } else {
                    None
                }
            case None => None
        }
    }

    def currentKey: Option[K] = {
        currentState match {
            case Some((key: K, _)) =>
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
        currentState = Some(key -> typeTag[T])
        state
    }

    def switch[T : TypeTag](key: K): Option[T] = {
        stateMap.get[T](key).map { state =>
            currentState = Some(key -> typeTag[T])
            state
        }
    }
}

object MultiTypeState {

    def apply[K, T : TypeTag](key: K, initialState: T): MultiTypeState[K] = {
        val state = new MultiTypeState[K]()
        state.put(key, initialState)
        state.currentState = Some(key, typeTag[T])
        state
    }
}
