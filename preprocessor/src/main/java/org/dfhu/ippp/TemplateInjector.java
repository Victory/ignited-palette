package org.dfhu.ippp;

import java.nio.charset.StandardCharsets;

public class TemplateInjector {
    private boolean inTag = false;
    private String curVar = null;

    private final byte START_TAG = '<';
    private final byte END_TAG = '>';
    private final byte[] VAR_PREFIX = " ip-var-".getBytes();
    private final byte NEW_LINE = '\n';

    private final String getFunctionName = "ipGetWithDefault";

    public String inject(String template) {
        BufferBuilder templateBuffer = new BufferBuilder(4000);
        BufferBuilder defaultValBuffer = null;

        templateBuffer.append('"');

        AttributeMatcher varMatcher = null;
        byte[] bytes = template.getBytes();
        for (byte ch: bytes) {
            if (match(NEW_LINE, ch)) {
                templateBuffer.append("\" +\n\"");
                continue;
            }

            if (match(START_TAG, ch)) {
                inTag = true;

                // print var part of the template
                if (curVar != null) {
                    templateBuffer.append('"');
                    templateBuffer.append(" + this.");

                    if (defaultValBuffer == null) {
                        // if we have no defaults just append the field directly
                        templateBuffer.append(curVar);
                    } else { // we have a defaults so need to call method
                        // build the call to getFunction
                        templateBuffer.append(getFunctionName);
                        templateBuffer.append('(');
                        templateBuffer.append("this.");
                        templateBuffer.append(curVar);
                        templateBuffer.append(", ");
                        templateBuffer.append(textNodeQuote(defaultValBuffer));
                        templateBuffer.append(')');
                        defaultValBuffer = null;
                    }

                    templateBuffer.append(" + ");
                    templateBuffer.append('"');
                }

                templateBuffer.append(ch);
                continue;
            }

            if (match(END_TAG, ch)) {
                inTag = false;
                templateBuffer.append(ch);
                if (varMatcher != null) {
                    curVar = varMatcher.getVar();
                }
                varMatcher = null;
                continue;
            }

            if (inTag) {
                templateBuffer.append(ch);
                if (varMatcher == null) {
                    varMatcher = new AttributeMatcher(VAR_PREFIX);
                }
                varMatcher.store(ch);
                continue;
            }

            if (!inTag) { // in a text node
                if (curVar != null) { // with default value
                    if (defaultValBuffer == null) {
                        defaultValBuffer = new BufferBuilder(400);
                    }
                    defaultValBuffer.append(ch);
                } else { // just normal html text with no var
                    templateBuffer.append(ch);
                }
            }

        }

        templateBuffer.append('"');
        templateBuffer.append('\0');
        return templateBuffer.toString().trim();
    }

    private byte[] textNodeQuote(BufferBuilder bb) {
        // XXX - needs to be optimized
        String tmp = bb.toString();
        tmp = tmp.replace("\n", "\\n");
        tmp = tmp.replace("\"", "\\\"");
        tmp = "\"" + tmp + "\"";
        return tmp.getBytes();
    }

    @SuppressWarnings("WeakerAccess")
    public static class BufferBuilder {
        private byte[] bb;
        private int curSize;
        private int index = 0;

        BufferBuilder(int initSize) {
            curSize = initSize;
            bb = new byte[curSize];
        }

        public void append(byte ch) {
            bb[index] = ch;
            index += 1;
        }

        public void append(char ch) {
            append((byte) ch);
        }

        public void append(String s) {
            for (byte ch: s.getBytes()) {
                append(ch);
            }
        }

        public void append(byte[] bytes) {
            for (byte ch: bytes) {
                if (ch == 0) {
                    return;
                }
                append(ch);
            }
        }

        public byte[] getBytes() {
           return bb;
        }

        public String toString() {
            bb[index] = 0;
            return new String(bb, 0, index, StandardCharsets.UTF_8);
        }
    }

    public static class AttributeMatcher {
        private int prefixIndex = 0;
        private final int maxVarSize = 129;
        private int bufferIndex = 0;
        private final byte[] prefix;
        private byte[] bb;
        private boolean foundTerminal = false;

        public AttributeMatcher(byte[] prefix) {
            this.prefix = prefix;
        }

        public void store(byte ch) {
            // check for a-zA-Z


            if (foundTerminal) {
                return;
            }

            if (prefixIndex >= prefix.length) {

                build(ch);
                return;
            }

            boolean found = prefix[prefixIndex] == ch;
            if (found) {
                prefixIndex += 1;
            } else {
                prefixIndex = 0;
                bufferIndex = 0;
            }
        }

        private void build(byte ch) {
            if (! ((ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122))) {
                // if we are inside a var then we found a terminal
                if (prefixIndex > 0) {
                    foundTerminal = true;
                }
                return;
            }

            if (bb == null) {
                bb = new byte[maxVarSize];
            }
            bb[bufferIndex] = ch;
            bufferIndex += 1;
        }

        public String getVar() {
            if (!hasVar()) {
                return null;
            }

            if (bufferIndex >= maxVarSize) {
                throw new RuntimeException("ip-var- variable length to long max size: " +
                        maxVarSize +
                        " " +
                        new String(bb, 0, maxVarSize - 5, StandardCharsets.UTF_8) + "...");
            }
            bb[bufferIndex] = 0;
            return new String(bb, 0, bufferIndex, StandardCharsets.UTF_8);
        }

        public boolean hasVar() {
            if (bb == null) {
                return false;
            }
            return true;
        }
    }

    private boolean match(byte lhs, byte rhs) {
        return lhs == rhs;
    }
}
