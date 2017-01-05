package org.dfhu.ippp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class TemplateInjector {
    private boolean inTag = false;
    private String curVar = null;

    private final byte START_TAG = '<';
    private final byte END_TAG = '>';
    private final byte DOUBLE_QUOTE = '"';
    private final String VAR_ATTR_NAME = "ip-var";
    private final byte NEW_LINE = '\n';
    private final byte CR = '\r';

    private final String getFunctionName = "ipGetWithDefault";

    public String onFly(String template) throws IOException {
        BufferBuilder templateBuffer = new BufferBuilder(4000);
        BufferBuilder defaultValBuffer = null;

        //templateBuffer.append('"');

        TagReader tagReader = null;
        // TODO make this a UNICODE point iteration
        byte[] bytes = template.getBytes();
        for (byte ch: bytes) {

            // ignore windows CR
            if (match(CR, ch)) {
                continue;
            }

            if (match(NEW_LINE, ch)) {
                System.out.println("appending new line");
                templateBuffer.append("\" +\n\"");
                continue;
            }

            if (match(START_TAG, ch)) {
                inTag = true;

                // print var part of the template
                if (curVar != null) {
                    //templateBuffer.append('"');
                    //templateBuffer.append(" + this.");

                    if (defaultValBuffer == null) {
                        // if we have no defaults just append the field directly
                        templateBuffer.append(curVar);
                    } else { // we have a defaults so need to call method
                        // build the call to getFunction

                        templateBuffer.append("#~#" + curVar + "#~#");
                        /*
                        templateBuffer.append(getFunctionName);
                        templateBuffer.append('(');
                        templateBuffer.append("this.");
                        templateBuffer.append(curVar);
                        templateBuffer.append(", ");
                        templateBuffer.append(textNodeQuote(defaultValBuffer));
                        templateBuffer.append(')');
                        */
                        defaultValBuffer = null;
                    }

                    //templateBuffer.append(" + ");
                    //templateBuffer.append('"');
                }

                templateBuffer.append(ch);
                continue;
            }

            // stop handling HTML tag
            if (match(END_TAG, ch)) {
                inTag = false;
                // store the > char
                templateBuffer.append(ch);

                if (tagReader != null) {
                    curVar = tagReader.getAttrs().get(VAR_ATTR_NAME);
                }
                tagReader = null;
                continue;
            }

            // handle HTML tag
            if (inTag) {

                // Add backslashes to double quotes
                if (match(DOUBLE_QUOTE, ch)) {
                    //templateBuffer.append('\\');
                }

                templateBuffer.append(ch);
                if (tagReader == null) {
                    tagReader = new TagReader();
                }
                tagReader.read(ch);
                continue;
            }

            if (!inTag) { // in a text node
                if (curVar != null) { // with default value
                    if (defaultValBuffer == null) {
                        defaultValBuffer = new BufferBuilder(400);
                    }
                    defaultValBuffer.append(ch);
                } else { // just normal html text with no var
                    // Add backslashes to double quotes

                    if (match(DOUBLE_QUOTE, ch)) {
                        templateBuffer.append('\\');
                    }
                    templateBuffer.append(ch);
                }
            }

        }

        //templateBuffer.append('"');
        return templateBuffer.toString().trim();
    }

    public String injectReplace(String template) throws IOException {
        template = onFly(template);
        template = template.replace("\"", "\\\"");
        Pattern pattern = Pattern.compile("#~#([a-z][0-9A-Za-z]+)#~#");

        Matcher matcher = pattern.matcher(template);
        if (matcher.find()) {
            template = matcher.replaceAll("\" + this.greeting + \"");
        }

        return '"' + template + '"';
    }

    public String inject(String template) throws IOException {
        BufferBuilder templateBuffer = new BufferBuilder(4000);
        BufferBuilder defaultValBuffer = null;

        templateBuffer.append('"');

        TagReader tagReader = null;
        // TODO make this a UNICODE point iteration
        byte[] bytes = template.getBytes();
        for (byte ch: bytes) {

            System.out.println(templateBuffer.toString());

            // ignore windows CR
            if (match(CR, ch)) {
                continue;
            }

            if (match(NEW_LINE, ch)) {
                System.out.println("appending new line");
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

            // stop handling HTML tag
            if (match(END_TAG, ch)) {
                inTag = false;
                // store the > char
                templateBuffer.append(ch);

                if (tagReader != null) {
                    curVar = tagReader.getAttrs().get(VAR_ATTR_NAME);
                }
                tagReader = null;
                continue;
            }

            // handle HTML tag
            if (inTag) {

                // Add backslashes to double quotes
                if (match(DOUBLE_QUOTE, ch)) {
                    templateBuffer.append('\\');
                }

                templateBuffer.append(ch);
                if (tagReader == null) {
                    tagReader = new TagReader();
                }
                tagReader.read(ch);
                continue;
            }

            if (!inTag) { // in a text node
                if (curVar != null) { // with default value
                    if (defaultValBuffer == null) {
                        defaultValBuffer = new BufferBuilder(400);
                    }
                    defaultValBuffer.append(ch);
                } else { // just normal html text with no var
                    // Add backslashes to double quotes

                    if (match(DOUBLE_QUOTE, ch)) {
                        templateBuffer.append('\\');
                    }
                    templateBuffer.append(ch);
                }
            }

        }

        templateBuffer.append('"');
        templateBuffer.append('\0');
        return templateBuffer.toString().trim();
    }

    private int[] textNodeQuote(BufferBuilder bb) {
        // XXX - needs to be optimized
        String tmp = bb.toString();
        tmp = tmp.replace("\n", "\\n");
        tmp = tmp.replace("\"", "\\\"");
        tmp = "\"" + tmp + "\"";

        int[] chars = new int[tmp.length()];
        int ii = 0;
        for (byte b: tmp.getBytes()) {
            chars[ii] = b;
            ii += 1;
        }
        return chars;
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
