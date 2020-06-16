package info.willdspann.generic

import java.time.LocalDate

object MultiTypeMapTest {

    def main(args: Array[String]): Unit = {
        val multiTypeMap = MultiTypeMap.empty[String]

        val eggs = "eggs"
        multiTypeMap.put("spam", eggs)

        val eggsReplaced = multiTypeMap.putIfAbsent("spam", "green eggs & ham")

        multiTypeMap.put("ham", "honey")
        val hamReplaced = multiTypeMap.put("ham", "eggs")

        multiTypeMap.putIfAbsent("parrot", "Pining for the fjords")

        multiTypeMap.put("number", 42)
        multiTypeMap.put("numbers", List(1, 1, 2, 3, 5, 8, 13))
        multiTypeMap.put("pair", 3 -> 'i')

        val marsBook = Book("Mars", "Ben Bova", LocalDate.of(1990, 6, 10), 532, eBook = true)
        multiTypeMap.put("mars", marsBook)

        val moonwarBook = Book("Moonwar", "Ben Bova", LocalDate.of(1985, 3, 1), 740, eBook = false)
        multiTypeMap.put("moonwar", moonwarBook)
        val moonwarReplaced = multiTypeMap.put("moonwar", "Moonwar (book)")

        println(s"Map size: ${multiTypeMap.size}")

        println(s"Contains 'spam': ${multiTypeMap.contains("spam")}")
        println(s"Value for 'spam' replaced? $eggsReplaced")

        multiTypeMap.get("spam").map { eggs: String =>
            println(s"spam: $eggs")
        }.getOrElse {
            println(s"spam: <null>")
        }

        val spam: String = multiTypeMap("spam")
        println(s"'spam' via apply(): $spam")

        println(s"Value for 'ham' replaced? $hamReplaced")

        println(s"Contains 'parrot': ${multiTypeMap.contains("parrot")}")

        multiTypeMap.get("parrot").map { parrot: String =>
            println(s"parrot: $parrot")
        }.getOrElse {
            println(s"parrot: <null>")
        }

        multiTypeMap.get("number").foreach { number: Int =>
            println(s"number: $number")
        }

        multiTypeMap.get("numbers").foreach { numbers: List[Int] =>
            println(s"numbers: $numbers")
        }

        println(s"Contains 'pair': ${multiTypeMap.contains("pair")}")
        multiTypeMap.get[(Int, Char)]("pair").foreach {
            case (i, c) => println(s"pair: $i -> $c")
        }

        val pair: (Int, Char) = multiTypeMap("pair")
        println(s"'pair' via apply(): ${pair._1} -> ${pair._2}")

        val (i: Int, c: Char) = multiTypeMap[(Int, Char)]("pair")
        println(s"'pair' destructured via apply(): $i -> $c")

        multiTypeMap.get("mars").foreach { book: Book =>
            println(s"mars: $book")
        }
        val marsBookRetrieved: Book = multiTypeMap("mars")
        println(s"'mars' via apply(): $marsBookRetrieved")

        println(s"Value for 'moonwar' replaced? $moonwarReplaced")

        multiTypeMap.get[Int]("spam").map { foo =>
            println(s"foo: $foo")
        }.getOrElse {
            println(s"No entry found with key: 'foo' and value type: Int")
        }

        multiTypeMap.get[Int]("foo").map { foo =>
            println(s"foo: $foo")
        }.getOrElse {
            println(s"No entry found with key: 'foo' and value type: Int")
        }

        multiTypeMap.get[List[Int]]("foo").map { foo =>
            println(s"foo: $foo")
        }.getOrElse {
            println(s"No entry found with key: 'foo' and value type: List[Int]")
        }

        println(s"Contains value 'eggs': ${multiTypeMap.containsValue("eggs")}")
        println(s"Contains (literal) value 'eggs': ${multiTypeMap.containsLiteral["eggs"]}")

        println(s"Contains [${multiTypeMap.count("eggs")}] entries with value: 'eggs'")
        println(s"Contains [${multiTypeMap.countLiteral["eggs"]}] entries with (literal) value: 'eggs'")

        println(s"Contains value 'Pining for the fjords': ${multiTypeMap.containsValue("Pining for the fjords")}")

        println(s"Contains value: 'foo': ${multiTypeMap.containsValue("foo")}")

        println(s"Contains value: 42 : ${multiTypeMap.containsValue(42)}")
        println(s"Contains (literal) value: 42 : ${multiTypeMap.containsLiteral[42]}")

        println(s"Contains value: 3 -> 'i' : ${multiTypeMap.containsValue(3 -> 'i')}")

        println(s"Contains value: $marsBook : ${multiTypeMap.containsValue(marsBook)}")

        println(s"Contains value: $moonwarBook : ${multiTypeMap.containsValue(moonwarBook)}")

        multiTypeMap.clear()
        println(s"Cleared map has [${multiTypeMap.size}] entries.")


        // Test the entries factory function.
        testEntriesFactory()
    }

    private def testEntriesFactory(): Unit = {
        val multiTypeMap = MultiTypeMap(
            "spam" -> "and eggs",
            "number" -> 42,
            "pair" -> (3 -> 'i'),
            "book" -> Book("Mars", "Ben Bova", LocalDate.of(1990, 6, 10), 532, eBook = true)
        )

        println(s"\nMultiTypeMap[String] built w/ new entries factory:\n  ${multiTypeMap.toString}")
    }

}
