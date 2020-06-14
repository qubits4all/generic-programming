package info.willdspann.generic

import shapeless.Generic

trait State {
    type Key
    type Value

    def getValue: Value
    def key: Key
}

object State {
    type Aux[K, V] = State { type Key = K; type Value = V }
}

case class SimpleState[K, V](
    override val key: K, value: V
) extends State {
    type Key = K; type Value = V

    override def getValue: V = value
}

class GenericState[K, V, R](override val key: K, private val value: V)
                           (implicit private val gen: Generic.Aux[V, R]) extends State
{
    type Key = K; type Value = V

    private val valueRepr: R = gen.to(value)

    def getValue: V = gen.from(valueRepr)

    def extractValue(implicit gen: Generic.Aux[V, R]): V = gen.from(valueRepr)
}

object GenericState {
    def apply[K, V, R](key: K, value: V)
                      (implicit gen: Generic.Aux[V, R]): GenericState[K, V, R] =
    {
        new GenericState(key, value)(gen)
    }
}
