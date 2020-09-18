package net.roguelogix.phosphophyllite.multiblock.generic;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ValidationError extends IllegalStateException {
    
    public ValidationError() {
        super();
    }
    
    public ValidationError(String s) {
        super(s);
    }
    
    public ValidationError(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ValidationError(Throwable cause) {
        super(cause);
    }
    
    ITextComponent cause;
    
    public ValidationError(ITextComponent cause) {
        super();
        this.cause = cause;
    }
    
    public ITextComponent getTextComponent() {
        if (cause != null) {
            return cause;
        }
        return new TranslationTextComponent(getMessage());
    }
}
