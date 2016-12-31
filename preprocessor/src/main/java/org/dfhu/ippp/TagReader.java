package org.dfhu.ippp;

import java.util.HashMap;
import java.util.Map;

public class TagReader {
    private static final byte EQ = '=';
    private static final byte[] QUOTES = new byte[]{'"', '\''};

    private static final Map<String, String> attrs = new HashMap<>();

    public void store(byte ch) {
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }
}
