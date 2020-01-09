package org.jetbrains.dukat.itertools.tests

import cartesian
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class ItertoolsTests {

    @Test
    fun cartesianEmptyList() {
        assertEquals(
                listOf(emptyList()),
                cartesian(emptyList<String>())
        )
    }

    @Test
    fun singleElementList() {
        assertEquals(
                listOf(emptyList()),
                listOf(emptyList<String>())
        )

        assertEquals(
                listOf(listOf("X")),
                cartesian(listOf("X"))
        )
    }

    @Test
    fun twoLists() {
        assertEquals(
                listOf(emptyList()),
                cartesian(emptyList(), listOf("b"))
        )

        assertEquals(
                listOf(emptyList()),
                cartesian(listOf("b"), emptyList())
        )

        assertEquals(
            listOf(listOf("a", "b")),
            cartesian(listOf("a"), listOf("b"))
        )

        assertEquals(
                listOf(listOf("a", "b"), listOf("a", "c")),
                cartesian(listOf("a"), listOf("b", "c"))
        )

        assertEquals(
                listOf(listOf("a", "b"), listOf("a", "c"), listOf("d", "b"), listOf("d", "c")),
                cartesian(listOf("a", "d"), listOf("b", "c"))
        )
    }

    @Test
    fun multipleSingleLists() {
        assertEquals(
                listOf(
                        listOf("I", "J", "I", "Q", "W")
                ),
                cartesian(listOf("I"), listOf("J"), listOf("I"), listOf("Q"), listOf("W"))
        )
    }

    @Test
    fun singleWithMultiple() {
        assertEquals(
                listOf(
                        listOf("a", "b"),
                        listOf("a", "c"),
                        listOf("a", "d")
                ),
                cartesian(listOf("a"), listOf("b", "c", "d"))
        )
    }

    @Test
    fun multipleLists() {
        assertEquals(
                listOf(
                        listOf("a", "c", "e"),
                        listOf("a", "c", "f"),
                        listOf("a", "d", "e"),
                        listOf("a", "d", "f"),
                        listOf("b", "c", "e"),
                        listOf("b", "c", "f"),
                        listOf("b", "d", "e"),
                        listOf("b", "d", "f")
                ),
                cartesian(listOf("a", "b"), listOf("c", "d"), listOf("e", "f"))
        )
    }

    @Test
    fun twoMultipleElementLists() {
        assertEquals(
                cartesian(listOf("a", "b", "c"), listOf("a", "b", "c")),
                listOf(
                    listOf("a", "a"),
                    listOf("a", "b"),
                    listOf("a", "c"),
                    listOf("b", "a"),
                    listOf("b", "b"),
                    listOf("b", "c"),
                    listOf("c", "a"),
                    listOf("c", "b"),
                    listOf("c", "c")
                )
        )
    }


}