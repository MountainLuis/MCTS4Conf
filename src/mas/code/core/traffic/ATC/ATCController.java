package mas.code.core.traffic.ATC;

import mas.code.core.framework.Scenario;
//import mas.code.core.traffic.ATC.MonteCarlo.ConflictResolveTree;
import mas.code.core.traffic.ATC.MonteCarloNew.ConflictResolveTree;
import mas.code.core.traffic.ATC.conflict.Conflict;
import mas.code.core.traffic.ATC.conflict.ConflictProbe;
import mas.code.core.traffic.ATC.conflict.ConflictSolveResult;
import mas.code.core.traffic.aircraft.AcftState;
import mas.code.core.traffic.aircraft.Aircraft;

public class ATCController {
    private ConflictProbe probe  = new ConflictProbe();

    private static final int conflictDetectPeriod = 60; //冲突探测时间间隔

    public void doStep(){
        int now = Scenario.clock.getTime();
        if (now % conflictDetectPeriod == 0){
            AcftState state0 = Scenario.aircraft0.lastPredictedState();
            AcftState state1 = Scenario.aircraft1.lastPredictedState();
            if (state0 == null || state1 == null){
                return;
            }
            if (state0.time != state1.time){
                System.err.println("Check time line" + state0.time + " " + state1.time);
                return;
            }
            Conflict conflict = probe.detect(state0,state1);
            if (conflict != null) {
                System.out.println(conflict.lateralType + " " + conflict.distance);
                Aircraft acft1 = conflict.acft1;
                Aircraft acft2 = conflict.acft2;
                ConflictResolveTree tree = new ConflictResolveTree(conflict,probe,Scenario.clock.getTimeStep());
                tree.iteration(1000);
                if (tree.result == ConflictSolveResult.Solved ){
                    acft1.prediction.clear();
                    acft1.prediction.addAll(tree.list1);
                    acft2.prediction.clear();
                    acft2.prediction.addAll(tree.list2);
                }
                tree.dumpResult();
            }

        }

    }

}
