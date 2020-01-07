package org.jetbrains.dukat.js.type.analysis

class PathWalker {
    private val directions = mutableListOf<Direction>()
    private var position = 0

    tailrec fun startNextPath() : Boolean {
        position = 0

        return if (directions.isNotEmpty()) {
            val lastDirection = directions[directions.lastIndex]

            if (lastDirection == Direction.Left) {
                directions[directions.lastIndex] = Direction.Right
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
            directions.add(Direction.Left)
        }

        return directions[position++]
    }

    enum class Direction {
        Left,
        Right
    }
}