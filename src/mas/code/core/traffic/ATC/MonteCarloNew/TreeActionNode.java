package mas.code.core.traffic.ATC.MonteCarloNew;

import mas.code.core.traffic.ATC.ATCCmd;
import mas.code.core.traffic.ATC.conflict.ConflictSolveResult;
import mas.code.core.traffic.aircraft.AcftState;
import mas.code.core.traffic.aircraft.Aircraft;
import mas.code.core.traffic.aircraft.TrajectoryEngine;

import java.util.ArrayList;
import java.util.List;

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
    List<TreeActionNode> children;
    boolean allChildrenExpanded;

    /**
     * use this constructor when node is root.
     * @param state1
     * @param state2
     * @param tree
     */
    public TreeActionNode(AcftState state1, AcftState state2, ConflictResolveTree tree){

        this.state1 = state1;
        this.state2 = state2;
        this.tree = tree;
        this.children = new ArrayList<>();
        this.parent = null;
        this.action = null;
        this.height = 0;
        this.allChildrenExpanded = false;
        this.value = 0.0;
        this.playCount = 0;
        this.okCount = 0;
        initRoot();
    }

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

    //这里发现的一个共性是，Root的children拿出的都是children，非root拿出的都是mChildren，因此只要在getChildren区分开就可以。
    @Override
    public List<TreeActionNode> getChildren() {
        if (this.parent == null){
            return (List<TreeActionNode>) children;
        }
        if (mChildren == null ||mChildren.isEmpty()){
            initChildren();
        }
        return mChildren;
    }

    @Override
    public boolean isAllChildrenExpanded() {
        return false;
    }

    private void checkResult(){
        if (result != null){
            return;
        }
        this.result = tree.probe.checkConflictSolved(tree.conflict,getState1(),getState2());
    }
    private void computeTracks(){
        TreeNode parent = this.parent;
        if(parent == null){   //todo 这里的问题：初始化Root之后，parent是null，要怎么传进来值。我用了一个不怎么好的方法。
            tracks1.addAll(computeTrack(this.state1, tracks1));
            tracks2.addAll(computeTrack(this.state2, tracks2));
        }else {
            tracks1.addAll(computeTrack(parent.getState1(), tracks1));
            tracks2.addAll(computeTrack(parent.getState2(), tracks2));
        }
        checkResult();
    }
    private List<AcftState> computeTrack(AcftState start,List<AcftState> trackList){
        List<AcftState> track = trackList;
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
        return track;
    }

    private void initChildren(){
         mChildren = new ArrayList<>();
         checkResult();
         if(result == ConflictSolveResult.Solved || result == ConflictSolveResult.Failed){
             return;
         }
         mChildren = treeFunction.computeChildren(this,mChildren);
    }
    private void initRoot(){

        children = treeFunction.computeChildren(this, (List<TreeActionNode>) children);
    }

    public ConflictSolveResult simulation(int maxHeight){
       if (this.height > maxHeight){
           return ConflictSolveResult.UnSolved;
       }
       checkResult(); //这里需要更新Result
       if(this.height == maxHeight){
           return result;
       }
       if(result != ConflictSolveResult.UnSolved){
           return result;
       }
        List<TreeActionNode> children = (List<TreeActionNode>) this.children;
        int idx = TreeNode.rd.nextInt(children.size());
       return children.get(idx).simulation(maxHeight); //这里用了一个递归，从上一层往下选了一个节点执行
    }

}
