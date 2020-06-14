package info.willdspann.generic

import shapeless.Generic

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.{TypeTag, typeOf}

class GenericMap[K] {
    private val map: mutable.Map[K, State] = mutable.HashMap.empty

    def get[V : ClassTag](key: K): Option[V] = {
        map.get(key).flatMap { state: State =>
            state.getValue match {
                case value: V => Some(value)
                case _ => None
            }
        }
    }

    def get[V : TypeTag, R](key: K)
                           (implicit gen: Generic.Aux[V, R]): Option[V] =
    {
        map.get(key).flatMap {
            case g: GenericState[K, _, _] if typeOf[V] == g.getValue.getClass =>
                Some(
                    g.asInstanceOf[GenericState[K, V, R]].extractValue(gen)
                )
            case _ => None
        }
    }

    def put[V, R](key: K, value: V)
                 (implicit gen: Generic.Aux[V, R]): Boolean =
    {
        val state = GenericState(key, value)
        map.put(key, state).isDefined
    }

    def putSimple[V : ClassTag](key: K, value: V): Boolean = {
        if (!hasTypeParameters(value)) {
            val state = SimpleState(key, value)
            map.put(key, state).isDefined
        }
        else {
            throw new IllegalArgumentException(
                s"Unsupported value type: ${value.getClass.getName} -- parameterized types are not supported."
            )
        }
    }

    private def isCaseClass[T](t: T): Boolean = {
        t.getClass.getInterfaces.contains(classOf[scala.Product])
    }

    private def hasTypeParameters[T](t: T): Boolean = {
        t.getClass.getTypeParameters.nonEmpty
    }
}
