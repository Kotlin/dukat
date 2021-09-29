package org.jetbrains.dukat.model.commonLowerings

typealias FileId = String
typealias LocalCollisions = Set<String>
typealias CollisionMap = MutableMap<FileId, LocalCollisions>

class CollisionContext {
    private val collisionMap: CollisionMap = mutableMapOf()

    fun getCollisionsForTheFile(id: FileId): LocalCollisions {
        return collisionMap.getOrDefault(id, emptySet())
    }

    fun putFileCollision(id: FileId, localCollisionSet: LocalCollisions) {
        collisionMap[id] = localCollisionSet
    }
}