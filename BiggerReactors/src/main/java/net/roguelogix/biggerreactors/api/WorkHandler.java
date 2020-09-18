package net.roguelogix.biggerreactors.api;

public class WorkHandler implements IWorkHandler {
    
    protected int progress;
    protected int goal;
    
    public WorkHandler(int goal) {
        this(goal, 0);
    }
    
    public WorkHandler(int goal, int progress) {
        this.progress = progress;
        this.goal = goal;
    }
    
    @Override
    public int getProgress() {
        return this.progress;
    }
    
    @Override
    public int getGoal() {
        return this.goal;
    }
    
    @Override
    public boolean isFinished() {
        return this.progress >= this.goal;
    }
    
    @Override
    public void increment(int amount) {
        this.progress += amount;
    }
    
    @Override
    public void decrement(int amount) {
        this.progress -= amount;
    }
    
    @Override
    public void clear() {
        this.progress = 0;
    }
}
