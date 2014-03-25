package cz.incad.kramerius.processes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;

import cz.incad.kramerius.processes.utils.PIDList;

public class GCFindCandiatesTask extends TimerTask {

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(GCFindCandiatesTask.class.getName());

    private LRProcessManager lrProcessManager;
    private GCScheduler gcScheduler;

    public GCFindCandiatesTask(LRProcessManager lrProcessManager,
            DefinitionManager definitionManager, GCScheduler gcScheduler,
            long interval) {
        super();
        this.lrProcessManager = lrProcessManager;
        this.gcScheduler = gcScheduler;
    }

    @Override
    public void run() {
        try {

            List<LRProcess> processesInStateSTARTED = lrProcessManager.getLongRunningProcesses(States.STARTED);
            List<LRProcess> processesInStateRUNNING = lrProcessManager.getLongRunningProcesses(States.RUNNING);

            List<String> gccCandidates = new ArrayList<String>();
            PIDList pidList = PIDList.createPIDList();
            List<String> pids = pidList.getProcessesPIDS();

            // check for killed processes
            for (LRProcess lrProcess : processesInStateRUNNING) {
                if (lrProcess.getPid() != null) {
                    if (!pids.contains(lrProcess.getPid())) {
                        // accessed pid but not in active running processes list 
                        gccCandidates.add(lrProcess.getUUID());
                    }
                } else {
                    LOGGER.log(Level.SEVERE,"the process "+lrProcess.getUUID()+" hasn't associated pid but it is in RUNNING state");
                }
            }
            // check for started but not running processes
            for (LRProcess lrProcess : processesInStateSTARTED) {
                if (lrProcess.getPid() == null) {
                    gccCandidates.add(lrProcess.getUUID());
                } else {
                        LOGGER.log(Level.SEVERE,"the process "+lrProcess.getUUID()+" has associated pid but not in RUNNING state");
                }
            }
            
            if (!gccCandidates.isEmpty()) {
                this.gcScheduler.scheduleCheckFoundGCCandidates(gccCandidates);
            } else {
                this.gcScheduler.scheduleFindGCCandidates();
            }

        } catch (Throwable e) {
            this.gcScheduler.shutdown();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
