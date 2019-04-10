package mas.code.core.traffic.ATC.MonteCarlo;

import kt.mas.core.traffic.aircraft.FlightPerformance;
import mas.code.core.traffic.ATC.ATCCmd;
import mas.code.core.traffic.ATC.AltCmd;
import mas.code.core.traffic.ATC.SpdCmd;
import mas.code.core.traffic.ATC.conflict.ConflictSolveResult;
import mas.code.core.traffic.aircraft.AcftState;
import mas.code.core.traffic.aircraft.Aircraft;
import mas.code.core.traffic.aircraft.TrajectoryEngine;
import mas.code.core.traffic.aircraft.VerticalState;

import java.util.ArrayList;
import java.util.List;
/*
public class TreeActionNode implements TreeNode{
    TreeFunction treeFunction = new TreeFunction();
    ConflictResolveTree tree;
    int height;
    ATCCmdAction action;
    TreeNode parent;
    double value;
    int playCount;
    int okCount;
    AcftState state1;
    AcftState state2;
    List<? extends TreeNode> children = new ArrayList<TreeActionNode>();

    boolean allChildrenExpanded;


    public TreeActionNode(ATCCmdAction action, TreeNode parent,ConflictResolveTree tree){
        this.action = action;
        this.parent = parent;
        this.tree = tree;
        this.height = parent.getHeight() + 1;
        this.value = 0;
        this.playCount = 0;
        this.okCount = 0;
        this.allChildrenExpanded = false;
    }
    public List<AcftState> tracks1 = new ArrayList<>();
    public List<AcftState> tracks2 = new ArrayList<>();
    public ConflictSolveResult result;

    List<TreeActionNode> mChildren;

    public TreeActionNode mostValuableChildren(){
        TreeActionNode ret = null;
        for (TreeNode c : children){
            if(ret == null){
                ret = (TreeActionNode)c;
            }else if(c.getValue() > ret.value && c.getValue() != 0.0){
                ret = (TreeActionNode)c;
            }
        }
        return ret;
    }

    public void checkAllChildrenExpanded(){
        if (children.isEmpty()){
            return;
        }
        boolean ok = true;
        for (TreeNode c : children){
            if (c.getPlayCount() == 0){
                ok = false;
                break;
            }
        }
        this.allChildrenExpanded = ok;
    }
    public void updateValue(double lnN){
        if (playCount == 0){
            this.value = 0;
            return;
        }
        double tmp = lnN * 2.0 /playCount;
        tmp = Math.sqrt(tmp);
        this.value = tmp + okCount/playCount;
    }

    @Override
    public ConflictResolveTree getTree() {
        return tree;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public ATCCmdAction getAction() {
        return action;
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int getPlayCount() {
        return playCount;
    }

    @Override
    public void setPlayCount(int count) {
            this.playCount = count;
    }

    @Override
    public int getOkCount() {
        return okCount;
    }

    @Override
    public void setOkCount(int count) {
            this.okCount = count;
    }

    public AcftState getState1(){
        if (tracks1.size() == 0){
            computeTracks();
        }
        return tracks1.get(tracks1.size()-1);
    }
    public AcftState getState2(){
        if (tracks2.size() == 0){
            computeTracks();
        }
        return tracks2.get(tracks2.size()-1);
    }

    @Override
    public List<TreeActionNode> getChildren() {
        if (mChildren != null){
            initChildren();
        }
        return mChildren;
    }

    @Override
    public boolean isAllChildrenExpanded() {
        return false;
    }


    private void checkResult(){
        if (result == null){
            return;
        }
        this.result = tree.probe.checkConflictSolved(tree.conflict,state1,state2);
    }
    private void computeTracks(){
        TreeNode parent = this.parent;
        computeTrack(parent.getState1(),tracks1);
        computeTrack(parent.getState2(),tracks2);
        checkResult();
    }
    private void computeTrack(AcftState start,List<AcftState> track){
        Aircraft acft = start.aircraft;
        TrajectoryEngine engine = acft.trajectoryEngine;
        double d = 0;
        AcftState prevState = start;
        ATCCmd cmd = null;
        if (action != null) {
            if(action.obj == acft){
                cmd = action.cmd;
            }
        }
        while (d < tree.nodeDuration){
            AcftState nextState = engine.doStep(prevState,cmd,tree.timeStep);
            track.add(nextState);
            prevState = nextState;
            if (nextState.finished){
                break;
            }
            cmd = null;
            d += tree.timeStep;
        }
    }

    private void initChildren(){
         mChildren = new ArrayList<>();
         checkResult();
         if(result == ConflictSolveResult.Solved || result == ConflictSolveResult.Failed){
             return;
         }
         treeFunction.computeChildren(this,mChildren);
    }

    public ConflictSolveResult simulation(int maxHeight){
       if (this.height > maxHeight){
           return ConflictSolveResult.UnSolved;
       }
       checkResult();
       if(this.height == maxHeight){
           return result;
       }
       if(result != ConflictSolveResult.UnSolved){
           return result;
       }
        List<TreeActionNode> children = (List<TreeActionNode>) this.children;  //我觉得这个玩意有问题
        int idx = TreeNode.rd.nextInt(children.size());
       return children.get(idx).simulation(maxHeight);
    }

}
*/