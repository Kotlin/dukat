package org.jetbrains.dukat.astCommon

class ReccurentEntitiesDetector<T> {
    private val recursiveTypes = mutableSetOf<T>();
    private val possibleRecursiveTypes = mutableMapOf<T, Int>();

    fun isAlreadyProcessed(entity: T?): Boolean {
        return entity != null && possibleRecursiveTypes.contains(entity)
    }

    fun wasRecursionFoundIn(entity: T?): Boolean {
        return recursiveTypes.contains(entity)
    }

    fun addToPossibleRecursiveEntities(entity: T?) {
        if (entity == null) return
        val count = possibleRecursiveTypes[entity] ?: 0

        if (count > 0) recursiveTypes.add(entity)

        possibleRecursiveTypes[entity] = count + 1
    }

    fun removeFromPossibleRecursiveEntities(entity: T?) {
        if (entity == null) return
        val count = possibleRecursiveTypes[entity] ?: return

        if (count == 1) {
            possibleRecursiveTypes.remove(entity)
        } else {
            possibleRecursiveTypes[entity] = count - 1
        }
    }

    inline fun <R> with(entity: T?, entityProcessing: (isAlreadyProcessed: Boolean) -> R): R {
        val isRecursive = isAlreadyProcessed(entity)
            .also { addToPossibleRecursiveEntities(entity) }

        return entityProcessing(isRecursive)
            .also { removeFromPossibleRecursiveEntities(entity) }
    }
}