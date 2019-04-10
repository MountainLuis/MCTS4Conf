package mas.code.core.traffic.ATC.conflict;

import demo.spatialindex.rtree.RTree;
import demo.spatialindex.rtree.Rect;
import demo.spatialindex.rtree.SpatialData;
import mas.code.core.misc.LogicPosition;
import mas.code.core.misc.LogicSegment;
import mas.code.core.traffic.aircraft.AcftState;
import mas.code.util.GeoUtil;

import java.util.*;

import static mas.code.util.Global.KM2M;

public class ConflictDetector {
    private static double LNG_GAP = 0.30522119469282;
    private static double LAT_GAP = 0.269494881006263;
    private static double ALT_GAP = 300;
    RTree tree;

    double sameRoutingThreshold = 10 * KM2M;
    double crossThreshold = 20 * KM2M;
    double oppositeThreshold = 30 * KM2M;

    public ConflictDetector(List<AcftState> states){
        tree = new RTree(9,states);
    }

    public static  List<Conflict> conflictDetect(List<AcftState> states ){
        List<Conflict> conflicts = new ArrayList<>();
        ConflictDetector detector = new ConflictDetector(states);
        for (int i = 0 ; i < states.size(); i++) {
            AcftState po = states.get(i);
            List<SpatialData> resList = detector.getCandidateSet(po);
            conflicts = detector.detectConflict(po,resList,conflicts);
        }
        return conflicts;
    }

    public List<SpatialData> getCandidateSet(AcftState state){
        Rect c = new Rect(state.location.lng-LNG_GAP,state.location.lng + LNG_GAP,
                state.location.lat-LAT_GAP,state.location.lat+LAT_GAP,
                state.alt - ALT_GAP, state.alt + ALT_GAP);
        List<SpatialData> candidate = tree.search(c);
        return candidate;
    }
    private List<Conflict> detectConflict(AcftState po,List<? extends SpatialData> resList,List<Conflict> conflicts){
        List<Conflict> res = conflicts;
        for (int i = 0; i < resList.size();i++){
            AcftState state1 = (AcftState) resList.get(i);
            Conflict ret = detect(po,state1);
            if (!res.contains(ret)){
                res.add(ret);
            }
        }
        return res;
    }

    private Conflict detect(AcftState state1, AcftState state2){
        if(state1.finished||state2.finished){
            return null;
        }
        if(Math.abs(state1.alt - state2.alt) >= 300){
            return null;
        }
        double dist = GeoUtil.calDist(state1.location,state2.location);
        if(dist > oppositeThreshold){
            return null;
        }
        LateralType lType = computeLateralType(state1, state2);
        if (lType == LateralType.Opposite){
            return new Conflict(state1,state2,lType,dist);
        }else if (lType == LateralType.Cross) {
            if (dist > crossThreshold) {
                return null;
            }else {
                return  new Conflict(state1,state2,lType,dist);
            }
        }else {
            if(dist > sameRoutingThreshold){
                return null;
            }else {
                AcftState s1 = state1;
                AcftState s2 = state2;
                double heading = GeoUtil.calHeading(s1.location,s2.location);
                if (Math.abs(GeoUtil.calIntersectionAngle(heading, s1.heading)) > 90){
                    s1 = state2;
                    s2 = state1;
                }
                return new Conflict(s1,s2,lType,dist);
            }
        }
    }
    private LateralType computeLateralType(AcftState state1, AcftState state2){
        LogicPosition pos1 = state1.pos;
        LogicPosition pos2 = state2.pos;

        if(pos1 == pos2){
            return LateralType.SameRouting;
        }
        LogicSegment seg1 = pos1.currentSegment;
        LogicSegment seg2 = pos2.currentSegment;
        System.out.println();
        if (seg1.equals(seg2)){
            return LateralType.SameRouting;
        }
        if (seg1.segment.equals(seg2.segment)){
//            System.out.println("targe 01");
            return LateralType.Opposite; //原程序这里有问题
        }
        if (pos2.getSegmentSize() > 1){
            if(pos2.getNextSegment() != null){
                LogicSegment _seg2 = pos2.getNextSegment();
                if(seg1.equals(_seg2)){
                    return LateralType.SameRouting;
                }
                if(seg1.segment.equals(_seg2.segment)){
                    System.out.println("targe 02");
                    return LateralType.Opposite;  //如果两航班计划有相同的一段但是航向相反，那应该是Opposite
                }
            }
            if(pos2.getPrevSegment() != null){
                LogicSegment _seg2 = pos2.getPrevSegment();
                if(seg1.equals(_seg2)){
                    return LateralType.SameRouting;
                }
                if(seg1.segment.equals(_seg2.segment)){
                    System.out.println("targe 03");
                    return LateralType.Opposite;
                }
            }

        }else if (pos1.getSegmentSize() > 1){
            if(pos1.getNextSegment() != null){
                LogicSegment _seg1 = pos1.getNextSegment();
                if(seg2.equals(_seg1)){
                    return LateralType.SameRouting;
                }
                if(seg2.segment.equals(_seg1.segment)){
                    System.out.println("targe 04");
                    return LateralType.Opposite;
                }
            }
            if(pos1.getPrevSegment() != null){
                LogicSegment _seg1 = pos1.getPrevSegment();
                if(seg2.equals(_seg1)){
                    return LateralType.SameRouting;
                }
                if(seg2.segment.equals(_seg1.segment)){
                    System.out.println("targe 05");
                    return LateralType.Opposite;
                }
            }
        }
        System.out.println("targe 06");
        return LateralType.Cross;
    }


}
