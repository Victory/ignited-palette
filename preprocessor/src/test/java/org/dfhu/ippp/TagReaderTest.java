package org.dfhu.ippp;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TagReaderTest {

    @Test
    public void noAttrs() {
        String input = "<p>";
        TagReader tagReader = buildTagReader(input);
        assertEquals(0, tagReader.getAttrs().size());
    }

    @Test
    public void endTag() {
        String input = "</p>";
        TagReader tagReader = buildTagReader(input);
        assertEquals(0, tagReader.getAttrs().size());
    }

    @Test
    public void getsOpeningTagName() {
        String input = "<p ignored-attr>";
        String expected = "p";

        TagReader tagReader = buildTagReader(input);
        String actual = tagReader.getTagName();
        assertEquals(expected, actual);
    }

    @Test
    public void getsOpeningTagNameNoAttrs() {
        String input = "<p>";
        String expected = "p";

        TagReader tagReader = buildTagReader(input);

        String actual = tagReader.getTagName();
        assertEquals(expected, actual);
    }

    @Test
    public void doubleQuoteAttrs() {
        String input = "<p foo=\"bar\">";
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("foo", "bar");

        TagReader tagReader = buildTagReader(input);

        assertEquals(tagReader.getAttrs().get("foo"), "bar");
    }

    @Test
    public void twoAttrs() {
        String input = "<p foo=\"bar\" biz=\"baz\">";
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("foo", "bar");
        expected.put("biz", "baz");

        TagReader tagReader = buildTagReader(input);

        assertEquals("bar", tagReader.getAttrs().get("foo"));
        assertEquals("baz", tagReader.getAttrs().get("biz"));
    }

    @Test
    public void multiLine() {
        String input = "<p foo=\"bar\" \n biz=\"baz\">";
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("foo", "bar");
        expected.put("biz", "baz");

        TagReader tagReader = buildTagReader(input);

        assertEquals("bar", tagReader.getAttrs().get("foo"));
        assertEquals("baz", tagReader.getAttrs().get("biz"));
    }

    private TagReader buildTagReader(String input) {
        StringReader sr = new StringReader(input);
        TagReader tagReader = new TagReader();

        try {
            int ch = sr.read();
            while (ch >= 0) {
                tagReader.read(ch);
                ch = sr.read();
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }

        return tagReader;
    }
}