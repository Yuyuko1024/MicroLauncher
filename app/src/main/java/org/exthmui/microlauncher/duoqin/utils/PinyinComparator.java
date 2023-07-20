package org.exthmui.microlauncher.duoqin.utils;

import java.util.Comparator;

public class PinyinComparator implements Comparator<Application> {

    @Override
    public int compare(Application o1, Application o2) {
        if (o1.getLetters().equals("@")
                || o2.getLetters().equals("#")) {
            return 1;
        } else if (o1.getLetters().equals("#")
                || o2.getLetters().equals("@")) {
            return -1;
        } else {
            return o1.getLetters().compareTo(o2.getLetters());
        }
    }
}
