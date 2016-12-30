package org.dfhu.ippp;

import java.nio.charset.StandardCharsets;

public class TemplateInjector {
    private boolean inTag = false;
    private final byte START_TAG = '<';
    private final byte END_TAG = '>';
    private final byte[] VAR_PREFIX = " ip-var-".getBytes();
    private final byte NEW_LINE = '\n';

    public String inject(String template) {
        BufferBuilder sb = new BufferBuilder(4000);
        sb.append('"');

        AttributeMatcher attributeMatcher = null;
        byte[] bytes = template.getBytes();
        for (byte ch: bytes) {
            if (match(NEW_LINE, ch)) {
                sb.append("\" +\n\"");
                continue;
            }

            if (match(START_TAG, ch)) {
                inTag = true;
                sb.append(ch);
                continue;
            }

            if (match(END_TAG, ch)) {
                inTag = false;
                sb.append(ch);
                if (attributeMatcher != null) {
                    String var = attributeMatcher.getVar();
                    if (var != null) {
                        sb.append('"');
                        sb.append(" + this.");
                        sb.append(var);
                        sb.append(" + ");
                        sb.append('"');
                    }
                }
                attributeMatcher = null;
                continue;
            }

            if (inTag) {
                sb.append(ch);
                if (attributeMatcher == null) {
                    attributeMatcher = new AttributeMatcher(VAR_PREFIX);
                }
                attributeMatcher.store(ch);
            } else { // in a text node
                sb.append(ch);
            }

        }

        sb.append('"');
        sb.append('\0');
        return sb.toString().trim();
    }

    public static class BufferBuilder {
        private byte[] bb;
        private int curSize;
        private int index = 0;

        public BufferBuilder(int initSize) {
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

        public String toString() {
            return new String(bb);
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
            if (bb == null) {
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
    }

    private boolean match(byte lhs, byte rhs) {
        return lhs == rhs;
    }
}
