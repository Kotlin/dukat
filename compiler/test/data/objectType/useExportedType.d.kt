package useExportedType

external interface StructureType {
    var name: String
    var details: String? get() = definedExternally; set(value) = definedExternally
}
external interface Registry {
    fun register(type: StructureType)
}
