package org.dfhu.ippp;

import org.junit.Test;

import static org.junit.Assert.*;

public class TemplateInjectorTest {

    @Test
    public void prefixMatch() {
        byte[] prefix = " ip-var-".getBytes();
        byte[] target = " ip-var-greeting".getBytes();
        TemplateInjector.AttributeMatcher attributeMatcher =
                new TemplateInjector.AttributeMatcher(prefix);
        for (byte ch: target) {
            attributeMatcher.store(ch);
        }

        String expected = "greeting";
        String actual = attributeMatcher.getVar();

        assertEquals(expected, actual);
    }

    @Test
    public void prefixMatch128CharVarName() {
        byte[] prefix = " ip-var-".getBytes();
        String expected = new String(new char[128]).replace("\0", "a");
        String stringTarget = " ip-var-" + expected;

        byte[] target = stringTarget.getBytes();

        TemplateInjector.AttributeMatcher attributeMatcher =
                new TemplateInjector.AttributeMatcher(prefix);
        for (byte ch: target) {
            attributeMatcher.store(ch);
        }

        String actual = attributeMatcher.getVar();

        assertEquals(expected, actual);
    }


    @Test
    public void prefixMatchNameTooLong() {
        byte[] prefix = " ip-var-".getBytes();
        String stringTarget = " ip-var-" + new String(new char[129]).replace("\0", "a");

        byte[] target = stringTarget.getBytes();

        TemplateInjector.AttributeMatcher attributeMatcher =
                new TemplateInjector.AttributeMatcher(prefix);
        for (byte ch: target) {
            attributeMatcher.store(ch);
        }

        try {
            String actual = attributeMatcher.getVar();
        } catch (RuntimeException e) {
            return;
        }
        fail("Runtime exception not thrown");
    }

    @Test
    public void prefixMatcherBadCharsRejected() {
        byte[] prefix = " ip-var-".getBytes();
        byte[] target = " ip-var-gre+foo".getBytes();
        TemplateInjector.AttributeMatcher attributeMatcher =
                new TemplateInjector.AttributeMatcher(prefix);
        for (byte ch: target) {
            attributeMatcher.store(ch);
        }

        String expected = "gre";
        String actual = attributeMatcher.getVar();

        assertEquals(expected, actual);
    }

    @Test
    public void oneLiner() {
        String input = "<p ip-var-greeting></p>";
        String expected = "\"<p ip-var-greeting>\" + this.greeting + \"</p>\"";
        String actual = new TemplateInjector().inject(input);
        assertEquals(expected, actual);
    }

    @Test
    public void oneLinerNoVar() {
        String input = "<p>Hi!</p>";
        String expected = endQuotes(input);
        String actual = new TemplateInjector().inject(input);
        assertEquals(expected, actual);
    }

    @Test
    public void twoLinerNoVar() {
        String input = "<p>I am line one</p>\n" +
                "<p>I am line two</p>";
        String exected = "\"<p> I am line one</p>\" +\n" +
                "\"<p>I am line two</p>\"";
        String actual = new TemplateInjector().inject(input);
        assertEquals(exected, actual);

    }

    private static String endQuotes(String input) {
        return '"' + input + '"';
    }
}