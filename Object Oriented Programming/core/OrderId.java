package m19.core;

import java.util.Comparator;

/*
 * Sort works ids from smallest to largest
 */
public class OrderId implements Comparator<Work> {
    @Override
    public int compare(Work first, Work second) {
        if (first.getId() > second.getId())
         return 1;
        if (first.getId() < second.getId())
        return -1;
        return 0;
    }
}