package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.block.Block;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.phosphophyllite.multiblock.generic.ValidationError;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;

public class InvalidBlock extends ValidationError {
    
    public InvalidBlock() {
        super();
    }
    
    public InvalidBlock(String s) {
        super(s);
    }
    
    public InvalidBlock(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidBlock(Throwable cause) {
        super(cause);
    }
    
    public InvalidBlock(Block block, Vector3i worldPosition, String multiblockPosition) {
        super(new TranslationTextComponent(
                "multiblock.error.phosphophyllite.invalid_block." + multiblockPosition,
                new TranslationTextComponent(block.getTranslationKey()),
                "(x: " + worldPosition.x + "; y: " + worldPosition.y + "; z: " + worldPosition.z + ")")
        );
    }
}
