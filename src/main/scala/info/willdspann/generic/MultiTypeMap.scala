package info.willdspann.generic

import scala.collection.mutable
import scala.reflect.runtime.universe.{TypeTag, typeTag, typeOf}

/**
 * Mutable hash-map supporting multiple value types. TypeTags are kept for all values stored and used to maintain
 * type safety on value retrieval and containment searches.
 */
class MultiTypeMap[K] {
    private val map: mutable.Map[K, Any] = mutable.HashMap.empty
    private val typesMap: mutable.Map[K, TypeTag[_ <: Any]] = mutable.HashMap.empty

    def put[V : TypeTag](key: K, value: V): Boolean = {
        typesMap.put(key, typeTag[V])
        map.put(key, value.asInstanceOf[Any]).isDefined
    }

    def putIfAbsent[V : TypeTag](key: K, value: V): Boolean = {
        if (!map.contains(key)) {
            put(key, value)
        }
        false
    }

    def remove(key: K): Boolean = {
        map.remove(key)
        typesMap.remove(key).isDefined
    }

    def apply[V : TypeTag](key: K): V = {
        get(key).getOrElse {
            throw new NoSuchElementException(s"No entry exists for key: $key and value type: ${typeOf[V]}")
        }
    }

    def get[V : TypeTag](key: K): Option[V] = {
        typesMap.get(key).flatMap { elemTypeTag =>
            if (typeOf[V] <:< elemTypeTag.tpe) {
                map.get(key).map(_.asInstanceOf[V])
            } else {
                None
            }
        }
    }

    def contains(key: K): Boolean = map.contains(key)

    def containsValue[V : TypeTag](value: V): Boolean = {
        // Find keys for values of matching type.
        val keyCandidates = typesMap.filter {
            case (_, ttag) => typeOf[V] <:< ttag.tpe
        }.map {
            case (key, _) => key
        }.toSet

        // Compare each value of matching type to given value.
        map.view.filterKeys { key =>
            keyCandidates.contains(key)
        }.exists {
            case (_, elem) => elem == value
        }
    }

    def containsLiteral[V : ValueOf](implicit ev: TypeTag[V]): Boolean = {
        // Find keys for values of matching type.
        val keyCandidates = typesMap.filter {
            case (_, ttag) => typeOf[V] <:< ttag.tpe
        }.map {
            case (key, _) => key
        }.toSet

        // Compare each value of matching type to given value.
        map.view.filterKeys { key =>
            keyCandidates.contains(key)
        }.exists {
            case (_, elem) => elem == valueOf[V]
        }
    }

    def size: Int = map.size

    def isEmpty: Boolean = map.isEmpty

    def nonEmpty: Boolean = map.nonEmpty
}
