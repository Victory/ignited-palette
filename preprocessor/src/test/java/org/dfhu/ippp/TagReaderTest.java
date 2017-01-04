package org.dfhu.ippp;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TagReaderTest {

    @Test
    public void noAttrs() {
        String input = "<p>";
        TagReader tagReader = new TagReader();
        for (byte ch: input.getBytes()) {
            tagReader.store(ch);
        }
        assertEquals(0, tagReader.getAttrs().size());
    }

    @Test
    public void endTag() {
        String input = "</p>";
        TagReader tagReader = new TagReader();
        for (byte ch: input.getBytes()) {
            tagReader.store(ch);
        }

        assertEquals(0, tagReader.getAttrs().size());
    }

    @Test
    public void getsOpeningTagName() {
        String input = "<p ignored-attr>";
        TagReader tagReader = new TagReader();
        String expected = "p";
        for (byte ch: input.getBytes()) {
            tagReader.store(ch);
        }
        String actual = tagReader.getTagName();
        assertEquals(expected, actual);
    }

    @Test
    public void getsOpeningTagNameNoAttrs() {
        String input = "<p>";
        TagReader tagReader = new TagReader();
        String expected = "p";
        for (byte ch: input.getBytes()) {
            tagReader.store(ch);
        }
        String actual = tagReader.getTagName();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoubleQuoteAttrs() {
        String input = "<p foo=\"bar\">";
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("foo", "bar");

        TagReader tagReader = new TagReader();
        for (byte ch: input.getBytes()) {
            tagReader.store(ch);
        }

        assertEquals(tagReader.getAttrs().get("foo"), "bar");
    }

    @Test
    public void testTwoAttrs() {
        String input = "<p foo=\"bar\" biz=\"baz\">";
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("foo", "bar");
        expected.put("biz", "baz");
        TagReader tagReader = new TagReader();
        for (byte ch: input.getBytes()) {
            tagReader.store(ch);
        }

        assertEquals(tagReader.getAttrs().get("foo"), "bar");
        assertEquals(tagReader.getAttrs().get("biz"), "baz");
    }
}