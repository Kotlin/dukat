@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import Album.AlbumLabel

typealias Playlist = (id: String, data: Any?) -> Unit

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface Album {
    var label: AlbumLabel
    open class AlbumLabel {
        open fun songsCount(): Number

        companion object {
            var defaultLabel: AlbumLabel
        }
    }

    companion object {
        fun play(album: Album, playlist: Playlist?)
    }
}