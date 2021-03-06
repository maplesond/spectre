/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 17/09/13
 * Time: 22:19
 * To change this template use File | Settings | File Templates.
 */
public class CollectionUtils {

    public static String[] setToStringArray(Set<String> set) {
        /* This function converts Set of Strings to an array of Strings */
        String[] array = new String[set.size()];
        Iterator<String> setIterator = set.iterator();
        for (int i = 0; i < array.length; i++) {
            array[i] = setIterator.next();
        }
        return array;
    }

    public static int[] setToIntArray(Set<Integer> set) {
        /* This function converts Set of Strings to an array of Strings */
        int[] array = new int[set.size()];
        Iterator<Integer> setIterator = set.iterator();
        for (int i = 0; i < array.length; i++) {
            array[i] = setIterator.next();
        }
        return array;
    }

    public static void sortIntArray(int[] arr) {
        if (arr.length == 4) {
            int a = arr[0];
            int b = arr[1];
            int c = arr[2];
            int d = arr[3];

            int min = (a < b) ? a : b;
            min = (min < c) ? min : c;
            min = (min < d) ? min : d;

            arr[0] = min;

            if (min == a) {
                a = b;
                b = c;
                c = d;
            } else if (min == b) {
                b = c;
                c = d;
            } else if (min == c) {
                c = d;
            }

            min = (a < b) ? a : b;
            min = (min < c) ? min : c;

            arr[1] = min;

            if (min == a) {
                a = b;
                b = c;
            } else if (min == b) {
                b = c;
            }

            arr[2] = (a < b) ? a : b;
            arr[3] = (a > b) ? a : b;
        } else {
            for (int i = 1; i < arr.length; i++) {
                for (int j = i; j > 0; j--) {
                    if (arr[j] < arr[j - 1]) {
                        int h = arr[j - 1];
                        arr[j - 1] = arr[j];
                        arr[j] = h;
                    }
                }
            }
        }
    }

    public static int nbTrueElements(boolean[] array) {
        int size = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == true) {
                size++;
            }
        }

        return size;
    }


    public static int[] getTrueElements(boolean[] set) {
        int size = nbTrueElements(set);
        int[] elements = new int[size];
        int j = 0;
        for (int i = 0; i < set.length; i++) {
            if (set[i] == true) {
                elements[j++] = i;
            }
        }
        return elements;
    }


    public static void swapTwoInAnArray(double[] array, int i1, int i2) {
        double tmp = array[i1];
        array[i1] = array[i2];
        array[i2] = tmp;
    }

    public static int findIndexOfMax(double[] array) {

        double maxVal = Double.NEGATIVE_INFINITY;
        int maxIndex = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxVal) {
                maxIndex = i;
                maxVal = array[i];
            }
        }

        return maxIndex;
    }


    public static List<Integer> convertIntArrayToLinkedList(int[] array) {

        List<Integer> list = new LinkedList<>();

        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }

        return list;
    }

    public static String doubleArrayToString(double[] array, String separator) {

        if (array == null || separator == null) {
            return null;
        }

        if (array.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(array[0]);

        for (int i = 1; i < array.length; i++) {
            sb.append(separator).append(array[i]);
        }

        return sb.toString();
    }

}
