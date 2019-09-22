package org.gameserver.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    public void testSearch() {
        int[] nums = new int[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 14, 15, 16, 18, 19};
        int i = search(nums, 11);
        System.out.println("index," + i + ",num" + nums[i]);
    }

    static int search(int[] s, int num) {

        int left = 0;
        int right = s.length;

        int mid;

        while (left <= right) {
            mid = (left + right) / 2;
            if (s[mid] > num) {
                right = mid - 1;
            } else if (s[mid] < num) {
                left = mid + 1;
            } else {
                return mid;
            }
        }
        return left;
    }




}
