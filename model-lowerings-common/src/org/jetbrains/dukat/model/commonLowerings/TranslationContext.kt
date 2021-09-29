package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.model.commonLowerings.overrides.InheritanceContext

class TranslationContext {
    private lateinit var _modelContext: ModelContext
    private lateinit var _collisionContext: CollisionContext
    private lateinit var _inheritanceContext: InheritanceContext

    val modelContext: ModelContext
        get() = _modelContext

    val collisionContext: CollisionContext
        get() = _collisionContext

    val inheritanceContext: InheritanceContext
        get() = _inheritanceContext

    fun initModelContext(modelContext: ModelContext) {
       _modelContext = modelContext
    }

    fun initInheritanceContext(inheritanceContext: InheritanceContext) {
        _inheritanceContext = inheritanceContext
    }

    fun initCollisionContext(collisionContext: CollisionContext) {
        _collisionContext = collisionContext
    }
}