package uk.ac.uea.cmp.spectre.core.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by dan on 11/03/14.
 */
public class StringUtilsTest {

    /*@Test
    public void testConvertNumbers1() {

        for(int i = 1; i < 300; i++) {
            System.out.println("Id: " + i + " - " + StringUtils.ConvertArabicToLetters(i));
        }
    }   */

    @Test
    public void testConvertNumbersLen1() {

        String str = StringUtils.ConvertArabicToLetters(3);

        assertTrue(str.equals("C"));
    }

    @Test
    public void testConvertNumbersLen2() {

        String str = StringUtils.ConvertArabicToLetters(27);

        assertTrue(str.equals("AA"));
    }
}
