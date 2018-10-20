package com.aihui.project.test;

import com.aihui.lib.base.util.FileUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void treeSet() {
        Set<Integer> set = new TreeSet<>();
        set.add(4);
        set.add(3);
        set.add(7);
        System.out.println(set.toString());
    }

    @Test
    public void arrayList() {
        A a = new A(1);
        List<A> aList = new ArrayList<>();
        aList.add(a);
        aList.add(a);
        aList.add(a);
        System.out.println(aList.toString());
    }

    @Test
    public void dayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println(day);
        int hour = (65697 + 15 + 484) % 24;
        System.out.println(TimeUnit.HOURS.toMillis(hour) + (long) (Math.random() * TimeUnit.MINUTES.toMillis(30)));
    }

    @Test
    public void test() {
        int a = 55;
        System.out.println(~a);
    }

    class A {
        int i;

        public A(int i) {
            this.i = i;
        }

        @Override
        public String toString() {
            return "A{" +
                    "i=" + i +
                    '}';
        }
    }
}