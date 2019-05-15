package org.jetbrains.dukat.interop.graal

import java.util.HashMap

class DocumentCache {
    private val myDocumentMap = HashMap<String, Any>()

    fun setDocument(key: String, path: String, sourceFile: Any) {
        myDocumentMap[path] = sourceFile
    }

    fun getDocument(key: String, path: String): Any? {
        return myDocumentMap[path]
    }
}
