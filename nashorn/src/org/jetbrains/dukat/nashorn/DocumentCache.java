package org.jetbrains.dukat.nashorn;

import java.util.HashMap;
import java.util.Map;

public class DocumentCache {
    private Map<String, Object> myDocumentMap = new HashMap<>();

    public void setDocument(String key, String path, Object sourceFile) {
        myDocumentMap.put(path, sourceFile);
    }

    public Object getDocument(String key, String path) {
        return myDocumentMap.get(path);
    }
}
