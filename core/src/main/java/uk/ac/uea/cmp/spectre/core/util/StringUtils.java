package uk.ac.uea.cmp.spectre.core.util;

/**
 * Created by dan on 11/03/14.
 */
public class StringUtils {

    public static String ConvertArabicToLetters(int num)
    {
        String letters = "";
        while (num > 0) {
            num--;
            letters = Character.toString((char) (65 + (num % 26))) + letters;
            num = num / 26;
        }
        return letters.toString();
    }
}
