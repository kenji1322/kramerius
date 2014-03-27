package cz.incad.kramerius.processes;

import java.util.List;

import cz.incad.kramerius.processes.utils.ProcessUtils;

/**
 * Processes states
 * 
 * @author pavels
 */
public enum States {

    /**
     * Not running process
     */
    NOT_RUNNING(0, true, "NOT_RUNNING","RUNNING"),

    /**
     * Running proces
     */
    RUNNING(1, false ,"RUNNING","FINISHED","FAILED","KILLED","WARNING"),

    /**
     * Correct finished proces
     */
    FINISHED(2, true,"FINISHED"),

    /**
     * FAiled with some errors
     */
    FAILED(3,true,"FAILED"),

    /**
     * Killed process
     */
    KILLED(4, true,"KILLED"),

    /**
     * Planned (process is waiting to start)
     */
    PLANNED(5,false,"PLANNED","STARTED"),

    /**
     * Finished with some errors
     */
    WARNING(9, true,"WARNING"),
    
    
    /**
     * Represents started state
     */
    STARTED(11, false, "STARTED","RUNNING","NOT_RUNNING"),
    
    /*
     * WARNING(10)
     */
    
    /**
     * Batch process started (contains child processes and all of them are
     * PLANNED or RUNNING).
     */
    @Deprecated
    BATCH_STARTED(6, false),

    
    /**
     * Batch process failed (some of child process FAILED)
     */
    @Deprecated
    BATCH_FAILED(7, false),

    /**
     * Batch process finished (all child processes finished with state FINISH)
     */
    @Deprecated
    BATCH_FINISHED(8,false);

        
    
    /**
     * Returns possible next states
     * @return
     */
    public String[] getNextState() {
        return this.nextStates;
    }
    
    
    /**
     * Returns true if this state is final state
     * @return
     */
    public boolean isFinalState() {
        return this.finalState;
    }
    

    /**
     * Load state from value
     * @param v Given value of state
     * @return
     */
    public static States load(int v) {
        for (States st : States.values()) {
            if (st.getVal() == v)
                return st;
        }
        return null;
    }

    private int val;
    private String[] nextStates;
    private boolean finalState;
    
    private States(int val, boolean finalState, String ... nextStates) {
        this.val = val;
        this.nextStates = nextStates;
        this.finalState = finalState;
    }

    public int getVal() {
        return val;
    }

    /**
     * Returns true if given transition (predecessor -> successor) is possible
     * @param predecessor The predecessor state
     * @param successor The successor state
     * @return
     */
    public static boolean isPossible(States predecessor, States successor) {
        String[] nextStateNames = predecessor.getNextState();
        for (String stName : nextStateNames) {
            if (successor.name().equals(stName)) return true;
        }
        return false;
    }
    

    /**
     * Returns true if one of given childStates contains any expecting state
     * @param childStates Child states
     * @param exp Expecting states
     * @return
     */
    public static boolean one(List<States> childStates, States... exp) {
        for (States st : childStates) {
            if (expect(st, exp))
                return true;
        }
        return false;
    }

    /**
     * Returns true if all of given child states contains any expecting state
     * @param childStates Child states
     * @param exp Execting state
     * @return
     */
    public static boolean all(List<States> childStates, States... exp) {
        for (States st : childStates) {
            if (!expect(st, exp))
                return false;
        }
        return true;
    }

    /**
     * Returns true if given real state is one of expecting state
     * @param real Real state
     * @param expected Expecting states
     * @return
     */
    public static boolean expect(States real, States... expected) {
        for (States exp : expected) {
            if (real.equals(exp))
                return true;
        }
        return false;
    }

    
    
    /**
     * Returns true given state is not running state
     * @param realState
     * @return
     */
    public static boolean notRunningState(States realState) {
        return expect(realState, States.FAILED, States.FINISHED, States.KILLED, States.NOT_RUNNING, States.WARNING, States.BATCH_FAILED, States.BATCH_FINISHED, States.BATCH_STARTED);
    }
}
