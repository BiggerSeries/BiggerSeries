package net.roguelogix.phosphophyllite.robn;

import java.util.ArrayList;
import java.util.Map;

public interface ROBNObject {
    default ArrayList<Byte> toROBN() {
        return ROBN.toROBN(toROBNMap());
    }
    
    Map<String, Object> toROBNMap();
    
    default void fromROBN(ArrayList<Byte> buf){
        Object robnObject = ROBN.fromROBN(buf);
        if(!(robnObject instanceof Map)){
            throw new IllegalArgumentException("Malformed binary");
        }
        // TODO: 7/26/20 check to make sure the keys are strings
        //noinspection unchecked
        fromROBNMap((Map<String, Object>) robnObject);
    }
    
    void fromROBNMap(Map<String, Object> map);
}
