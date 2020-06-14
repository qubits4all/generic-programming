package info.willdspann.generic

import java.time.LocalDate
import java.util.UUID

import shapeless.Generic

object GenericMapTest {

    def main(args: Array[String]): Unit = {
        val uuid: SimpleState[String, UUID] = SimpleState("spam", UUID.randomUUID())
        val genericBook = new GenericState(
            "mars",
            Book("Mars", "Ben Bova", LocalDate.of(1990, 6, 10), 532, eBook = true)
        )

        println(s"${uuid.key}: ${uuid.getValue.toString}")
        println(s"${genericBook.key}: ${genericBook.getValue}")

        val genMap: GenericMap[String] = new GenericMap()
        genMap.putSimple[UUID]("spam", UUID.randomUUID())
        genMap.put(
            "mars",
            Book("Mars", "Ben Bova", LocalDate.of(1990, 6, 10), 532, eBook = true)
        )

        implicit val bookGen = Generic.apply[Book]

        val maybeUuid: Option[UUID] = genMap.get[UUID]("spam")
        maybeUuid.foreach { uuid =>
            println(s"Retrieved UUID for key 'spam': $uuid")
        }

        val maybeBook: Option[Book] = genMap.get[Book, bookGen.Repr]("mars")
        maybeBook.foreach { book =>
            println(s"Retrieved Book for key 'mars': $book")
        }

        val maybeBookToo: Option[Book] = genMap.get[Book]("mars")
        maybeBookToo.foreach { book =>
            println(s"Retrieved Book also for key 'mars': $book")
        }

        val missingString: Option[String] = genMap.get[String]("blah")
        println(s"Missing string: $missingString")

        val missingUuid: Option[UUID] = genMap.get[UUID]("blah")
        println(s"Missing UUID: $missingUuid")

        val missingBook: Option[Book] = genMap.get[Book]("blah")
        println(s"Missing book w/out Generic: $missingBook")

        val missingBook2: Option[Book] = genMap.get[Book, bookGen.Repr]("blah")
        println(s"Missing book: $missingBook2")
    }
}

case class Book(title: String, author: String, publishedDate: LocalDate, pageCount: Int, eBook: Boolean)
