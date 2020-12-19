package net.roguelogix.phosphophyllite.util;


import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeSet;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AStarList {
    
    ArrayList<BlockPos> targets = new ArrayList<>();
    TreeSet<BlockPos> nodeSet = new TreeSet<>(this::orderingFunction);
    
    private int orderingFunction(BlockPos a, BlockPos b) {
        if(targets.isEmpty()){
            return 0;
        }
        BlockPos target = targets.get(0);
        double aDistance = a.distanceSq(target);
        double bDistance = b.distanceSq(target);
        int aHash = a.hashCode();
        int bHash = b.hashCode();
        return aDistance < bDistance ? -1 : (bDistance == aDistance ? Integer.compare(aHash, bHash) : 1);
    }
    
    public void addTarget(BlockPos target) {
        if (targets.isEmpty()) {
            nodeSet.add(target);
        }
        if (!targets.contains(target)) {
            targets.add(target);
        }
    }
    
    public void addNode(BlockPos node) {
        targets.remove(node);
        nodeSet.add(node);
    }
    
    public BlockPos nextNode() {
        return Objects.requireNonNull(nodeSet.pollFirst());
    }
    
    public boolean done() {
        return targets.isEmpty() || nodeSet.isEmpty();
    }
    
    public boolean foundAll() {
        return targets.isEmpty();
    }
}
