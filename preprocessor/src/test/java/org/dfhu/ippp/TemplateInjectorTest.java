package org.dfhu.ippp;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TemplateInjectorTest {

    /*
    @Test
    public void prefixMatch() {
        byte[] prefix = " ip-var-".getBytes();
        byte[] target = " ip-var=\"greeting\"".getBytes();
        TemplateInjector.AttributeMatcher attributeMatcher =
                new TemplateInjector.AttributeMatcher(prefix);
        for (byte ch: target) {
            attributeMatcher.read(ch);
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
            attributeMatcher.read(ch);
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
            attributeMatcher.read(ch);
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
            attributeMatcher.read(ch);
        }

        String expected = "gre";
        String actual = attributeMatcher.getVar();

        assertEquals(expected, actual);
    }
    */

    @Test
    public void oneLiner() throws IOException {
        String input = "<p ip-var=\"greeting\"></p>";
        String expected = "\"<p ip-var=\\\"greeting\\\">\" + this.greeting + \"</p>\"";
        String actual = new TemplateInjector().inject(input);
        assertEquals(expected, actual);
    }

    @Test
    public void oneLinerNoVar() throws IOException {
        String input = "<p>Hi!</p>";
        String expected = endQuotes(input);
        String actual = new TemplateInjector().inject(input);
        assertEquals(expected, actual);
    }

    @Test
    public void twoLinerNoVar() throws IOException {
        String input = "<p>I am line one</p>\n" +
                "<p>I am line two</p>";
        String expected = "\"<p>I am line one</p>\" +\n" +
                "\"<p>I am line two</p>\"";
        String actual = new TemplateInjector().inject(input);
        assertEquals(expected, actual);
    }

    @Test
    public void twoLinerWithVar() throws IOException {
        String input = "<p>I am line one</p>\n" +
                "<p ip-var=\"lineTwo\"></p>";
        String expected = "\"<p>I am line one</p>\" +\n" +
                "\"<p ip-var=\\\"lineTwo\\\">\" + this.lineTwo + \"</p>\"";
        String actual = new TemplateInjector().inject(input);
        assertEquals(expected, actual);
    }

    @Test
    public void basicExampleWithVar() throws IOException {
        String input = "<div>\n" +
                "  <p ip-var=\"greeting\"></p>\n" +
                "</div>";
        String expected = "\"<div>\" +\n" +
                "\"  <p ip-var=\\\"greeting\\\">\" + this.greeting + \"</p>\" +\n" +
                "\"</div>\"";

        String actual = new TemplateInjector().inject(input);
        assertEquals(expected, actual);
    }

    @Test
    public void handleDefaultText() throws IOException {
        String input = "<p ip-var=\"greeting\">Show Default</p>";
        String expected =  "\"<p ip-var=\\\"greeting\\\">\" + this.ipGetWithDefault(this.greeting, \"Show Default\") + \"</p>\"";
        String actual = new TemplateInjector().inject(input);
        assertEquals(expected, actual);
    }

    @Test
    public void handleDefaultTextWithQuotes() throws IOException {
        String input = "<p ip-var=\"greeting\">Show \"Default\"</p>";
        String expected =  "\"<p ip-var=\\\"greeting\\\">\" + this.ipGetWithDefault(this.greeting, \"Show \\\"Default\\\"\") + \"</p>\"";
        String actual = new TemplateInjector().inject(input);
        assertEquals(expected, actual);
    }

    private static String endQuotes(String input) {
        return '"' + input + '"';
    }
}