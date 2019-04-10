package mas.code.core.traffic.ATC.MonteCarlo;

import kt.mas.core.traffic.aircraft.FlightPerformance;
import mas.code.core.traffic.ATC.AltCmd;
import mas.code.core.traffic.ATC.SpdCmd;
import mas.code.core.traffic.aircraft.AcftState;
import mas.code.core.traffic.aircraft.Aircraft;
import mas.code.core.traffic.aircraft.VerticalState;

import java.util.List;
/*
public class TreeFunction {

    public  TreeActionNode mostValuableChildren (TreeNode  tNode){
        TreeActionNode ret = null;
        for (TreeNode c : tNode.getChildren()){
            if(ret == null){
                ret = (TreeActionNode)c;
            }else if(c.getValue() > ret.value && c.getValue() != 0.0){
                ret = (TreeActionNode)c;
            }
        }
        return ret;
    }

    public void computeChildren(TreeNode node,List<TreeActionNode> mChildren){
        mChildren.add(new TreeActionNode(null,node,node.getTree()));
        computeAltActionChildren(node.getState1(),node,mChildren);
        computeAltActionChildren(node.getState2(),node,mChildren);
        computeSpdActionChildren(node.getState1(),node,mChildren);
        computeSpdActionChildren(node.getState2(),node,mChildren);
    }

    private void computeAltActionChildren(AcftState state, TreeNode node, List<TreeActionNode> mChildren){
        Aircraft acft = state.aircraft;
        double RFL = (acft.curFltPlan!=null)?acft.curFltPlan.RFL:null;  //check this sentence
        ConflictResolveTree tree = node.getTree();
        double maxAlt = Math.min(RFL + 1800,tree.maxAlt);
        double minAlt = Math.max(RFL-1800,tree.minAlt);
        if(state.altCmd != null){
            if(state.altCmd.delta > 0){
                minAlt = Math.max(minAlt,state.altCmd.targetAlt);
            }else {
                maxAlt = Math.min(maxAlt,state.altCmd.targetAlt);
            }
        }
        //without target altitude
//        if(state.altCmd != null){
//            if(state.altCmd.delta > 0){
//                minAlt = Math.max(minAlt,state.alt + state.altCmd.delta);
//            }else {
//                maxAlt = Math.min(maxAlt,state.alt + state.altCmd.delta);
//            }
//        }

        int dAlt1 = (int) (minAlt - RFL);
        dAlt1 = (int) (dAlt1/tree.altStep *tree.altStep);

        double alt = RFL + dAlt1;
        while (alt <= maxAlt){
            ATCCmdAction action = new ATCCmdAction(acft,new AltCmd(0,alt-RFL,alt));
            mChildren.add(new TreeActionNode(action,node,node.getTree()));
            alt += tree.altStep;
        }
    }

    private void computeSpdActionChildren(AcftState state, TreeNode node, List<TreeActionNode> mChildren){
        Aircraft acft = state.aircraft;
        FlightPerformance performance = state.performance;
        ConflictResolveTree tree = node.getTree();
        double maxSpd;
        double minSpd;
        if (state.vState == VerticalState.Climb){
            maxSpd = performance.getMaxClimbTAS();
            minSpd = performance.getMinClimbTAS();
        }else if (state.vState == VerticalState.Cruise){
            maxSpd = performance.getMaxCruiseTAS();
            minSpd = performance.getMinCruiseTAS();
        }else {
            maxSpd = performance.getMaxDescentTAS();
            minSpd = performance.getMinDescentTAS();
        }
        double baseSpd = state.hSpdTAS;
        if(state.spdCmd != null){
            baseSpd = state.spdCmd.targetTAS;
            if (state.spdCmd.delta > 0){
                minSpd = Math.max(minSpd,state.spdCmd.targetTAS);
            }else {
                maxSpd = Math.min(maxSpd,state.spdCmd.targetTAS);
            }
        }
        double delta = baseSpd - 5 * tree.spdStep;
        while (true){
            double spd = baseSpd + delta;
            if (spd > maxSpd){
                break;
            }
            if (spd < minSpd){
                delta += tree.spdStep;
                continue;
            }
            ATCCmdAction action = new ATCCmdAction(acft,new SpdCmd(0,delta,spd));
            mChildren.add(new TreeActionNode(action,node,node.getTree()));
            delta += tree.spdStep;
        }
    }




}
*/