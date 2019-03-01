package com.aihui.lib.base.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 胡一鸣 on 2019/1/22.
 */
public final class ArrayUtils {

    public static List<Integer> array2List(int... array) {
        List<Integer> list = new ArrayList<>(array.length);
        for (int value : array) {
            list.add(value);
        }
        return list;
    }

    public static int[] list2Array(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            int value = list.get(i);
            array[i] = value;
        }
        return array;
    }
}
