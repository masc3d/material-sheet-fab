package org.masc.util;

public class Cast {
    /**
     * Cast helper
     */
    @SuppressWarnings("unchecked")
    public static <V> V as(Object obj, Class<V> v) {
        if (obj != null && v.isAssignableFrom(obj.getClass()))
            return (V) obj;
        else return null;
    }
}
