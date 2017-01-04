package org.dfhu.ippp;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TagReader {
    private static final byte EQ = '=';
    private static final byte END = '/';
    private static final byte TAG_START = '<';
    private static final byte TAG_END = '>';
    private static final byte[] TAGENDS = new byte[]{TAG_END, TAG_START};

    private static final byte[] QUOTES = new byte[]{'"', '\''};
    private static final Map<String, String> attrs = new HashMap<>();
    private static final byte SPC = ' ';
    private static final byte[] SPACES = new byte[]{' ', '\t', '\r', '\n'};

    private boolean foundTagName = false;
    private boolean inAttrKey = false;
    private boolean inAttrVal = false;
    private boolean isEndTag = false;

    // TODO make growing bytes array thing
    private byte[] tagName = new byte[300];
    private int tagNameIndex = 0;

    public void store(byte ch) {
        if (match(ch, TAGENDS)) {
            return;
        }

        // ignore end tags
        if (isEndTag) {
            return;
        }

        // todo ByteBuffery object
        if (!foundTagName && !match(ch, SPACES)) {
            tagName[tagNameIndex] = ch;
            tagNameIndex += 1;
            tagName[tagNameIndex] = 0;
            return;
        }

        // TODO store value of match(ch, SPACES);
        if (!foundTagName && match(ch, SPACES)) {
            foundTagName = true;
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

    /**
     * If no tag name then retur nnew String(null, 0, 0, UTF_8)
     * @return - tag name as a string
     */
    public String getTagName() {
        return new String(tagName, 0, tagNameIndex, StandardCharsets.UTF_8);
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
