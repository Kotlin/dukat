package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration

val eventSpecifierMapper = mapOf(
        "onbeforeunload" to "BeforeUnloadEvent",

        "ondrag" to "DragEvent",
        "ondragend" to "DragEvent",
        "ondragenter" to "DragEvent",
        "ondragexit" to "DragEvent",
        "ondragleave" to "DragEvent",
        "ondragover" to "DragEvent",
        "ondragstart" to "DragEvent",
        "ondrop" to "DragEvent",

        "oncopy" to "ClipboardEvent",
        "oncut" to "ClipboardEvent",
        "onpaste" to "ClipboardEvent",


        "onfetch" to "FetchEvent",

        "onblur" to "FocusEvent",
        "onfocus" to "FocusEvent",

        "onhashchange" to "HashChangeEvent",

        "oninput" to "InputEvent",

        "onkeydown" to "KeyboardEvent",
        "onkeypress" to "KeyboardEvent",
        "onkeyup" to "KeyboardEvent",

        "onmessage" to "MessageEvent",

        "onclick" to "MouseEvent",
        "oncontextmenu" to "MouseEvent",
        "ondblclick" to "MouseEvent",
        "onmousedown" to "MouseEvent",
        "onmouseenter" to "MouseEvent",
        "onmouseleave" to "MouseEvent",
        "onmousemove" to "MouseEvent",
        "onmouseout" to "MouseEvent",
        "onmouseover" to "MouseEvent",
        "onmouseup" to "MouseEvent",

        "onnotificationclick" to "NotificationEvent",
        "onnotificationclose" to "NotificationEvent",

        "onpagehide" to "PageTransitionEvent",
        "onpageshow" to "PageTransitionEvent",

        "ongotpointercapture" to "PointerEvent",
        "onlostpointercapture" to "PointerEvent",
        "onpointercancel" to "PointerEvent",
        "onpointerdown" to "PointerEvent",
        "onpointerenter" to "PointerEvent",
        "onpointerleave" to "PointerEvent",
        "onpointermove" to "PointerEvent",
        "onpointerout" to "PointerEvent",
        "onpointerover" to "PointerEvent",
        "onpointerup" to "PointerEvent",

        "onpopstate" to "PopStateEvent",

        "onloadstart" to "ProgressEvent",
        "onprogress" to "ProgressEvent",

        "onunhandledrejection" to "PromiseRejectionEvent",

        "onstorage" to "StorageEvent",

        "onwheel" to "WheelEvent"
)


data class EventMapKey(val name: String, val context: String)

val eventSpecifierMapperWithContext = mapOf(
        EventMapKey("onaddtrack", "MediaStream") to "MediaStreamTrackEvent",
        EventMapKey("onremovetrack", "MediaStream") to "MediaStreamTrackEvent",

        EventMapKey("onaddtrack", "AudioTrackList") to "TrackEvent",
        EventMapKey("onaddtrack", "TextTrackList") to "TrackEvent",
        EventMapKey("onaddtrack", "VideoTrackList") to "TrackEvent",
        EventMapKey("onremovetrack", "AudioTrackList") to "TrackEvent",
        EventMapKey("onremovetrack", "TextTrackList") to "TrackEvent",
        EventMapKey("onremovetrack", "VideoTrackList") to "TrackEvent"
)

private class EventHandlerSpecifier : IDLLowering {

    private var currentInterfaceName: String? = null

    override fun lowerAttributeDeclaration(declaration: IDLAttributeDeclaration, owner: IDLFileDeclaration): IDLAttributeDeclaration {
        if (declaration.type !is IDLFunctionTypeDeclaration || currentInterfaceName == null) {
            return declaration
        }
        val newEventName = eventSpecifierMapper[declaration.name]
                ?: eventSpecifierMapperWithContext[EventMapKey(declaration.name, currentInterfaceName!!)]
        if (newEventName != null) {
            val oldType = declaration.type as IDLFunctionTypeDeclaration
            return declaration.copy(
                    type = oldType.copy(
                            arguments = oldType.arguments.map {
                                if (it.type == IDLSingleTypeDeclaration("Event", null, false)) {
                                    it.copy(
                                            type = IDLSingleTypeDeclaration(newEventName, null, false)
                                    )
                                } else {
                                    it
                                }
                            }
                    )
            )
        }
        return declaration
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        currentInterfaceName = declaration.name
        val result = super.lowerInterfaceDeclaration(declaration, owner)
        currentInterfaceName = null
        return result
    }

}

fun IDLSourceSetDeclaration.specifyEventHandlerTypes(): IDLSourceSetDeclaration {
    return EventHandlerSpecifier().lowerSourceSetDeclaration(this)
}