package org.dfhu.ippp;

import java.util.HashMap;
import java.util.Map;

public class TagReader {
    private static final int EQ = '=';
    private static final int END = '/';
    private static final int TAG_START = '<';
    private static final int TAG_END = '>';
    private static final int[] TAG_ENDINGS = new int[]{TAG_END, TAG_START};

    private static final int[] QUOTES = new int[]{'"', '\''};
    private static final int[] SPACES = new int[]{' ', '\t', '\r', '\n'};

    private boolean foundTagName = false;
    private boolean inAttrKey = false;
    private boolean haveAttrKey = false;
    private boolean inAttrVal = false;

    private boolean isEndTag = false;

    private BufferBuilder tagName = new BufferBuilder(40);

    private BufferBuilder curAttrKey;
    private BufferBuilder curAttrVal;

    // attr key value pairs
    private Map<String, String> attrs = new HashMap<>();

    /**
     * Process the next byte of a HTML tag if
     * @param ch - byte to process
     */
    public void store(int ch) {
        if (match(ch, TAG_ENDINGS)) {
            return;
        }

        // ignore end tags
        if (isEndTag) {
            return;
        }

        boolean isMatchSpaces = match(ch, SPACES);

        // End of tagName
        if (!foundTagName && isMatchSpaces) {
            foundTagName = true;
            return;
        }

        // spaces between attributes
        if (!haveAttrKey && isMatchSpaces) {
            return;
        }

        // storing the tag name
        if (!foundTagName && !isMatchSpaces) {
            tagName.append(ch);
            return;
        }

        // this is an end tag
        if (match(ch, END) && !foundTagName) {
            isEndTag = true;
            return;
        }

        // Store attribute value
        if (haveAttrKey) {
            if (match(ch, QUOTES)) {
                // see if we found the ending quote
                if (inAttrVal) {
                    attrs.put(curAttrKey.toString(), curAttrVal.toString());
                    curAttrKey = null;
                    curAttrVal = null;
                    inAttrVal = false;
                    haveAttrKey = false;
                } else {
                    inAttrVal = true;
                }
                return;
            }
            if (curAttrVal == null) {
                curAttrVal = new BufferBuilder(30);
            }
            curAttrVal.append(ch);
        }

        // searching for next attribute
        if (foundTagName && curAttrKey == null) {
            curAttrKey = new BufferBuilder(30);
            curAttrKey.append(ch);
            return;
        }

        // Store or Terminate current attry key
        if (foundTagName && curAttrKey != null && !haveAttrKey) {
            if (match(ch, EQ)) {
                haveAttrKey = true;
            } else {
                curAttrKey.append(ch);
            }
            return;
        }


        // get ready for new key, value or end
        if (isMatchSpaces && !inAttrVal) {
        }
    }

    /**
     * If no tag name then return new String(new byte[size]{}, 0, 0, UTF_8)
     * @return - tag name as a string
     */
    public String getTagName() {
        return tagName.toString();
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    private boolean match(int lhs, int rhs) {
        return lhs == rhs;
    }

    private boolean match(int lhs, int[] target) {
        for (int rhs: target) {
           if (rhs == lhs) {
               return true;
           }
        }
        return false;
    }
}
