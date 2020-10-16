package net.roguelogix.phosphophyllite.multiblock.generic;

import javax.annotation.Nonnull;

public interface Validator<T> {
    
    boolean validate(@Nonnull T t);
    
    static <T> Validator<T> and(@Nonnull Validator<T> left, @Nonnull Validator<T> right) {
        return (t) -> left.validate(t) && right.validate(t);
    }
    
    static <T> Validator<T> or(@Nonnull Validator<T> left, @Nonnull Validator<T> right) {
        return (t) -> left.validate(t) || right.validate(t);
    }
}
