package info.willdspann.generic.state

import scala.reflect.runtime.universe.{TypeTag, typeOf, typeTag}

case class KeyedState[K, V : TypeTag](key: K, value: V, valueType: TypeTag[V])

case class KeyedStateWrapper[K, V](key: K, state: KeyedState[K, V])
