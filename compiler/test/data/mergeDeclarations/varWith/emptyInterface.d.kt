package emptyInterface

external interface I {
    companion object : I by definedExternally {
    }
}
