package org.dfhu.ippp;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

// http://www.fileformat.info/info/charset/UTF-8/list.htm
public class Utf8IOTest {

    @Test
    public void readCodePoints() throws IOException {
        String input = "\u55B0\u55B4";
        String inputPasted = "喰喴";
        assertEquals(inputPasted, input);

        /*
        input = new String(new int[] { 67088 }, 0, 1);

        input = new String(new int[] { 0xFFFF + 3 }, 0, 1);
        */

        StringReader sr = new StringReader(input);
        StringBuilder sb = new StringBuilder();

        int read = sr.read();
        int numTimesRun = 0;
        while (read >= 0) {
            numTimesRun += 1;
            sb.appendCodePoint(read);
            read = sr.read();
        }

        assertEquals(2, numTimesRun);
        assertEquals(input, new String(sb));
    }

    @Test
    public void biggerThanFFFF() throws IOException {
        String input = new String(new int[] { 0xFFFF + 3 }, 0, 1);

        StringReader sr = new StringReader(input);
        StringBuilder sb = new StringBuilder();

        int read = sr.read();
        int numTimesRun = 0;
        while (read >= 0) {
            numTimesRun += 1;
            sb.appendCodePoint(read);
            read = sr.read();
        }

        assertEquals(2, numTimesRun);
        assertEquals(input, new String(sb));
    }
}
