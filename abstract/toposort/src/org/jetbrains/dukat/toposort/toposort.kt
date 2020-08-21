package org.jetbrains.dukat.toposort

private enum class ToposortEdgeStatus {
    PROCESSED,
    PROCESSING
}

private fun <T> toposortStep(vertice: T, processStatus: MutableMap<T, ToposortEdgeStatus>, output: MutableList<T>, adjacentVertex: (T) -> Iterable<T> ) {
    if (processStatus[vertice] == ToposortEdgeStatus.PROCESSED) {
        return
    }

    if (processStatus[vertice] == ToposortEdgeStatus.PROCESSING) {
        //TODO:  actually this means a loop and we should fail
        return
    }

    processStatus[vertice] = ToposortEdgeStatus.PROCESSING

    adjacentVertex(vertice).forEach { toposortStep(it, processStatus, output, adjacentVertex)  }
    processStatus[vertice] = ToposortEdgeStatus.PROCESSED

    output.add(vertice)
}

fun <T> Iterable<T>.toposort(adjacentVertex: (T) -> Iterable<T>): List<T> {
    val output = mutableListOf<T>()
    val processStatus = mutableMapOf<T, ToposortEdgeStatus>()

    forEach { vertice ->
        toposortStep(vertice, processStatus, output) { v -> adjacentVertex(v) }
    }

    return output
}