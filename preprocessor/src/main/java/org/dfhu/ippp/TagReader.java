package org.dfhu.ippp;

import java.util.HashMap;
import java.util.Map;

public class TagReader {
    private static final byte EQ = '=';
    private static final byte END = '/';

    private static final byte[] QUOTES = new byte[]{'"', '\''};
    private static final Map<String, String> attrs = new HashMap<>();
    private static final byte SPC = ' ';
    private static final byte[] SPACES = new byte[]{' ', '\t', '\r', '\n'};

    private boolean foundTagName = false;
    private boolean inAttrKey = false;
    private boolean inAttrVal = false;
    private boolean isEndTag = false;

    public void store(byte ch) {
        // ignore end tags
        if (isEndTag) {
            return;
        }

        // this is an end tag
        if (match(ch, END) && !foundTagName) {
            isEndTag = true;
            return;
        }

        // get ready for new key, value or end
        if (match(ch, SPACES) && !inAttrVal) {
        }
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    private boolean match(byte lhs, byte rhs) {
        return lhs == rhs;
    }

    private boolean match(byte lhs, byte target[]) {
        for (byte rhs: target) {
           if (rhs == lhs) {
               return true;
           }
        }
        return false;
    }
}
