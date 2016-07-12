package sx.android;

/**
 * Created by n3 on 12/07/16.
 */
public interface Function<F, T> {
    T apply(F input);
    @Override
    boolean equals(Object object);
}