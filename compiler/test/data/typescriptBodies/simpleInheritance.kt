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

open class Animal(open var name: Any) {
    open fun move(meters: Any) {
        console.log(this.name.asDynamic() + " moved " + meters.asDynamic() + "m.");
    }
}

open class Snake(_name: Any) : Animal(_name) {
    open fun move() {
        console.log("Slithering...");
        super.move(5);
    }
}

open class Horse(_name: Any) : Animal(_name) {
    open fun move() {
        console.log("Galloping...");
        super.move(45);
    }
}

var sam = Snake("Sammy the Python")

var tom: Animal = Horse("Tommy the Palomino")

fun main() {
    sam.move()
    tom.move(45)
}