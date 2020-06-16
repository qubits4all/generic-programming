package info.willdspann.generic

import java.util.Objects

import scala.collection.mutable
import scala.reflect.runtime.universe.{TypeTag, typeOf, typeTag}

/**
 * Mutable hash-map supporting multiple value types. TypeTags are kept for all values stored and used to maintain
 * type safety on value retrieval and containment searches.
 */
class MultiTypeMap[K] {
    private val map: mutable.Map[K, Any] = mutable.HashMap.empty
    private val typesMap: mutable.Map[K, TypeTag[_ <: Any]] = mutable.HashMap.empty

    def this(it: IterableOnce[(K, _ <: Any)]) = {
        this()
        it.iterator.foreach { case (key, value) =>
            put(key, value)
        }
    }

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

    def clear(): Unit = {
        map.clear()
        typesMap.clear()
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

        // Find the first value of matching type equal to the given value.
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

        // Find the first value of matching type equal to the given value.
        map.view.filterKeys { key =>
            keyCandidates.contains(key)
        }.exists {
            case (_, elem) => elem == valueOf[V]
        }
    }

    def count[V : TypeTag](value: V): Int = {
        // Find keys for values of matching type.
        val keyCandidates = typesMap.filter {
            case (_, ttag) => typeOf[V] <:< ttag.tpe
        }.map {
            case (key, _) => key
        }.toSet

        // Count the number of values of matching type equal to the given value.
        map.view.filterKeys { key =>
            keyCandidates.contains(key)
        }.count {
            case (_, elem) => elem == value
        }
    }

    def countLiteral[V : ValueOf](implicit ev: TypeTag[V]): Int = {
        // Find keys for values of matching type.
        val keyCandidates = typesMap.filter {
            case (_, ttag) => typeOf[V] <:< ttag.tpe
        }.map {
            case (key, _) => key
        }.toSet

        // Count the number of values of matching type equal to the given value.
        map.view.filterKeys { key =>
            keyCandidates.contains(key)
        }.count {
            case (_, elem) => elem == valueOf[V]
        }
    }

    def size: Int = map.size

    def isEmpty: Boolean = map.isEmpty

    def nonEmpty: Boolean = map.nonEmpty

    override def equals(obj: Any): Boolean = {
        if (obj == this) return true
        if (obj == null) return false

        obj match {
            case m: MultiTypeMap[_] =>
                m.typesMap.equals(this.typesMap) && m.map.equals(this.map)
            case _ => false
        }
    }

    override def hashCode(): Int = {
        Objects.hash(typesMap, map)
    }

    override def toString: String = map.toString()
}

object MultiTypeMap {

    def apply[K](): MultiTypeMap[K] = empty[K]

    def apply[K](entries: (K, _ <: Any)*): MultiTypeMap[K] = {
        new MultiTypeMap[K](entries)
    }

    def empty[K]: MultiTypeMap[K] = {
        new MultiTypeMap[K]()
    }
}
