package org.jetbrains.dukat.js.type.analysis.tests

import org.jetbrains.dukat.js.type.analysis.PathWalker
import org.jetbrains.dukat.js.type.analysis.PathWalker.Direction.Left
import org.jetbrains.dukat.js.type.analysis.PathWalker.Direction.Right
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private data class BinaryTreeNode (
        val children: Pair<BinaryTreeNode, BinaryTreeNode>? = null
) {
    constructor(left: BinaryTreeNode, right: BinaryTreeNode) : this(left to right)

    val isEndNode: Boolean
        get() = children == null

    val pathCount: Int
        get() : Int {
            return children?.let { it.first.pathCount + it.second.pathCount } ?: 1
        }

    operator fun get(direction: PathWalker.Direction) : BinaryTreeNode {
        return when (direction) {
            Left -> children!!.first
            Right -> children!!.second
        }
    }
}

class PathWalkerTests {

    private fun getPaths(rootNode: BinaryTreeNode) : List<List<PathWalker.Direction>> {
        val paths: MutableList<List<PathWalker.Direction>> = mutableListOf()
        var currentPath: MutableList<PathWalker.Direction> = mutableListOf()

        val pathWalker = PathWalker()

        val pathCount = rootNode.pathCount
        var visitedPaths = 0

        var currentNode = rootNode

        do {
            currentNode = if (currentNode.isEndNode) {
                val isTraversalDone = !pathWalker.startNextPath()
                paths += currentPath
                currentPath = mutableListOf()
                visitedPaths++

                if (visitedPaths < pathCount) {
                    assert(!isTraversalDone) { "Traversal terminated early! Expected $pathCount paths, but visited $visitedPaths in tree:\n$rootNode" }
                } else {
                    assert(isTraversalDone) { "Traversal failed to terminate correctly in tree:\n$rootNode" }
                }

                rootNode
            } else {
                val nextDirection = pathWalker.getNextDirection()
                currentPath.add(nextDirection)
                currentNode[nextDirection]
            }
        } while (visitedPaths < pathCount)

        return paths
    }

    private fun getMessage(tree: BinaryTreeNode) = "Tree failed to be walked correctly:\n$tree\n"

    private fun assertTreeWalkResult(tree: BinaryTreeNode, result: List<List<PathWalker.Direction>>) {
        assertEquals(
                expected = result,
                actual = getPaths(tree),
                message = getMessage(tree)
        )
    }

    @Test
    fun singleNodeTree() {
        assertTreeWalkResult(
                tree = BinaryTreeNode(),
                result = listOf(emptyList())
        )
    }

    @Test
    fun simpleBalancedTree() {
        assertTreeWalkResult(
                tree = BinaryTreeNode(
                        left = BinaryTreeNode(
                                left = BinaryTreeNode(
                                        left = BinaryTreeNode(
                                                left = BinaryTreeNode(),
                                                right = BinaryTreeNode()
                                        ),
                                        right = BinaryTreeNode(
                                                left = BinaryTreeNode(),
                                                right = BinaryTreeNode()
                                        )
                                ),
                                right = BinaryTreeNode(
                                        left = BinaryTreeNode(
                                                left = BinaryTreeNode(),
                                                right = BinaryTreeNode()
                                        ),
                                        right = BinaryTreeNode(
                                                left = BinaryTreeNode(),
                                                right = BinaryTreeNode()
                                        )
                                )
                        ),
                        right = BinaryTreeNode(
                                left = BinaryTreeNode(
                                        left = BinaryTreeNode(
                                                left = BinaryTreeNode(),
                                                right = BinaryTreeNode()
                                        ),
                                        right = BinaryTreeNode(
                                                left = BinaryTreeNode(),
                                                right = BinaryTreeNode()
                                        )
                                ),
                                right = BinaryTreeNode(
                                        left = BinaryTreeNode(
                                                left = BinaryTreeNode(),
                                                right = BinaryTreeNode()
                                        ),
                                        right = BinaryTreeNode(
                                                left = BinaryTreeNode(),
                                                right = BinaryTreeNode()
                                        )
                                )
                        )
                ),
                result = listOf(
                        listOf(Left, Left, Left, Left),
                        listOf(Left, Left, Left, Right),
                        listOf(Left, Left, Right, Left),
                        listOf(Left, Left, Right, Right),
                        listOf(Left, Right, Left, Left),
                        listOf(Left, Right, Left, Right),
                        listOf(Left, Right, Right, Left),
                        listOf(Left, Right, Right, Right),
                        listOf(Right, Left, Left, Left),
                        listOf(Right, Left, Left, Right),
                        listOf(Right, Left, Right, Left),
                        listOf(Right, Left, Right, Right),
                        listOf(Right, Right, Left, Left),
                        listOf(Right, Right, Left, Right),
                        listOf(Right, Right, Right, Left),
                        listOf(Right, Right, Right, Right)
                )
        )
    }

    @Test
    fun basicTreeLeft() {
        assertTreeWalkResult(
                tree = BinaryTreeNode(
                        left = BinaryTreeNode(
                                left = BinaryTreeNode(
                                        left = BinaryTreeNode(),
                                        right = BinaryTreeNode()
                                ),
                                right = BinaryTreeNode()
                        ),
                        right = BinaryTreeNode()
                ),

                result = listOf(
                        listOf(Left, Left, Left),
                        listOf(Left, Left, Right),
                        listOf(Left, Right),
                        listOf(Right)
                )
        )
    }

    @Test
    fun basicTreeRight() {
        assertTreeWalkResult(
                tree = BinaryTreeNode(
                        left = BinaryTreeNode(),
                        right = BinaryTreeNode(
                                left = BinaryTreeNode(),
                                right = BinaryTreeNode(
                                        left = BinaryTreeNode(),
                                        right = BinaryTreeNode()
                                )
                        )
                ),

                result = listOf(
                        listOf(Left),
                        listOf(Right, Left),
                        listOf(Right, Right, Left),
                        listOf(Right, Right, Right)
                )
        )
    }

}