package org.dfhu.ippp;


import java.io.IOException;
import java.io.StringReader;

public class BufferBuilder {

    private final StringBuilder stringBuilder;

    BufferBuilder(int initSize) {
        stringBuilder = new StringBuilder(initSize);
    }

    public void append(int ch) {
        stringBuilder.appendCodePoint(ch);
    }

    public void append(String s) throws IOException {
        StringReader sr = new StringReader(s);

        int ch = sr.read();
        while (ch >= 0) {
            append(ch);
            ch = sr.read();
        }
    }

    public void append(int[] chars) {
        for (int ch: chars) {
            append(ch);
        }
    }

    /** Returns byte buffer as UTF_8 String */
    public String toString() {
        return stringBuilder.toString();
    }
}
