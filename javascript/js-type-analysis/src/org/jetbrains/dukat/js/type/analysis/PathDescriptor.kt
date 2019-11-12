package org.jetbrains.dukat.js.type.analysis

class PathWalker {
    private val directions = mutableListOf<Direction>()
    private var position = 0

    tailrec fun startNextPath() : Boolean {
        position = 0

        return if (directions.lastIndex >= 0) {
            val lastDirection = directions[directions.lastIndex]

            if (lastDirection == Direction.First) {
                directions[directions.lastIndex] = Direction.Second
                true
            } else {
                directions.removeAt(directions.lastIndex)
                startNextPath()
            }
        } else {
            false
        }
    }

    fun getNextDirection() : Direction {
        if (directions.size <= position) {
            directions.add(Direction.First)
        }

        return directions[position++]
    }

    enum class Direction {
        First,
        Second
    }
}