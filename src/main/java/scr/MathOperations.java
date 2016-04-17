package scr;

import java.util.*;

/**
 * Created by p.bell on 19.04.2015.
 */
public class MathOperations {
    //public static final long SEED=23;
    public static List sortByValueAsc(final Map m) {
        List keys = new ArrayList();
        keys.addAll(m.keySet());
        Collections.sort(keys, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object v1 = m.get(o1);
                Object v2 = m.get(o2);
                if (v1 == null) {
                    return (v2 == null) ? 0 : 1;
                } else if (v1 instanceof Comparable) {
                    return ((Comparable) v1).compareTo(v2) * 1;
                } else {
                    return 0;
                }
            }
        });
        return keys;
    }
}
