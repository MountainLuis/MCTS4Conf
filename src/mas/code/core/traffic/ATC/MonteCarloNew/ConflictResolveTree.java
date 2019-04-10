package mas.code.core.traffic.ATC.MonteCarloNew;

import mas.code.core.traffic.ATC.ATCCmd;


import mas.code.core.traffic.ATC.conflict.Conflict;
import mas.code.core.traffic.ATC.conflict.ConflictProbe;
import mas.code.core.traffic.ATC.conflict.ConflictSolveResult;
import mas.code.core.traffic.aircraft.AcftState;
import mas.code.util.Global;

import java.util.ArrayList;
import java.util.List;

public class ConflictResolveTree {
    TreeFunction treeFunction = new TreeFunction();
    Conflict conflict;
    ConflictProbe probe;
    int timeStep;

    double maxAlt = 12000.0;
    double minAlt = 6000.0;
    double altStep = 600;
    double spdStep = 10 * Global.KT2MPS;
    int nodeDuration = 60;

    TreeActionNode root;  //root
    int totalCount = 0;
    public ConflictSolveResult result;
    public List<AcftState> list1 = new ArrayList<>();
    public List<AcftState> list2 = new ArrayList<>();
    public List<ATCCmd> cmdList1 = new ArrayList<>();
    public List<ATCCmd> cmdList2 = new ArrayList<>();

    public ConflictResolveTree( Conflict conflict,ConflictProbe probe, int timeStep){
            this.conflict = conflict;
            this.probe = probe;
            this.timeStep = timeStep;
            root = new TreeActionNode(conflict.state1.aircraft.curState,conflict.state2.aircraft.curState,this);
//            root.init
    }

    public void dumpResult(){
        System.out.println("Result " + result);
        System.out.println("Cmd1 : ");
        for (ATCCmd c : cmdList1){
            System.out.println("\t" + c);
        }
        System.out.println("Cmd2 : ");
        for (ATCCmd c : cmdList2){
            System.out.println("\t" + c);
        }
    }
    public void iteration(int step){
        for (int i = 0; i < step;i++){
            totalCount = i+1;
            iterationStep();
        }
        chooseResult();
    }

    public void chooseResult(){
        TreeActionNode node = this.root;

        for (int i = 0; i < 5; i++){
            if (node != null) {
                node = treeFunction.mostValuableChildren(node);//  todo
                if(node == null){
                    break;
                }
            }
            if(node.getAction() != null){
                if(node.getAction().obj == conflict.acft1){
                    cmdList1.add(node.getAction().cmd);
                }else {
                    cmdList2.add(node.getAction().cmd);
                }
            }
                list1.addAll(node.tracks1);
                list2.addAll(node.tracks2);
                this.result = node.result;
                if (node.result != ConflictSolveResult.UnSolved) {
                    break;
                }
        }

    }
    private void iterationStep(){
        TreeActionNode node = root;
        boolean ok = false;
        while (true){
            if ( node.getChildren().isEmpty()){
                break;
            }
            if (node.isAllChildrenExpanded()){  //todo
                node = treeFunction.mostValuableChildren(node);
                continue;
            }
            int idx = TreeNode.rd.nextInt( node.getChildren().size());
            node = node.getChildren().get(idx);
            ok = node.simulation(6) == ConflictSolveResult.Solved;
            break;
        }
        if (node.getParent() != null){
            if (node.getParent() instanceof TreeActionNode){
                ((TreeActionNode) node.getParent()).checkAllChildrenExpanded();
            }
        }
        TreeNode n = node;
        while (n != null){
            n.setPlayCount(n.getPlayCount() + 1);
            if (ok){
                n.setOkCount(n.getOkCount()+1);
            }
            if (n instanceof TreeActionNode){
                double lnN = Math.log(totalCount);
                ((TreeActionNode) n).updateValue(lnN);
            }
            n = n.getParent();
        }
    }



}
