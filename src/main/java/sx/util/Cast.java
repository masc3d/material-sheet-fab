package sx.util;

public class Cast {
    /**
     * Cast helper
     */
    @SuppressWarnings("unchecked")
    public static <V> V as(Class<V> v, Object obj) {
        if (obj != null && v.isAssignableFrom(obj.getClass()))
            return (V) obj;
        else return null;
    }
}
