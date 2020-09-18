package net.roguelogix.phosphophyllite.multiblock.generic;

public interface Validator<T> {
    
    boolean validate(T t);
    
    static <T> Validator<T> and(Validator<T> left, Validator<T> right) {
        return (T t) -> left.validate(t) && right.validate(t);
    }
    
    static <T> Validator<T> or(Validator<T> left, Validator<T> right) {
        return (T t) -> left.validate(t) || right.validate(t);
    }
}
