package mas.code.core.traffic.ATC.conflict;

import mas.code.core.misc.LogicPosition;
import mas.code.core.misc.LogicSegment;
import mas.code.core.traffic.aircraft.AcftState;
import mas.code.util.GeoUtil;
import mas.code.util.Global;

import static mas.code.util.Global.KM2M;

public class ConflictProbe {

    double sameRoutingThreshold = 10 * KM2M;
    double crossThreshold = 20 * KM2M;
    double oppositeThreshold = 30 * KM2M;

    // 不区分冲突类型的探测、
    public Conflict simpleDetect(AcftState state1,AcftState state2){
        if(state1.finished||state2.finished){
            return null;
        }
        if(Math.abs(state1.alt - state2.alt) >= 300){
            return null;
        }
        double dist = GeoUtil.calDist(state1.location,state2.location);
        if(dist > sameRoutingThreshold){
            return null;
        }
        System.out.println(dist + " dist ");
        return new Conflict(state1,state2,LateralType.Cross,dist);
    }


    public Conflict detect(AcftState state1, AcftState state2){
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
    public ConflictSolveResult checkConflictSolved(Conflict c, AcftState state1, AcftState state2){
        if(Math.abs(state1.alt - state2.alt) >= 300){    //建立高度差判断
            return ConflictSolveResult.Solved;
        }
        double dist = GeoUtil.calDist(state1.location,state2.location);
        double threshold = 0.0;
        if (c.lateralType == LateralType.Cross){
            threshold = crossThreshold;
        }else if (c.lateralType == LateralType.SameRouting) {
            threshold = sameRoutingThreshold;
        }else {
            threshold = oppositeThreshold;
        }
        if (dist < threshold){   //是否已经小于临界距离判断
            return ConflictSolveResult.Failed;
        }
        if (c.lateralType == LateralType.Opposite){  //对头冲突已经相遇过
            double h = GeoUtil.calHeading(state1.location, state2.location);
            if (Math.abs(GeoUtil.calIntersectionAngle(h,state1.heading)) > 90){
                return ConflictSolveResult.Solved;
            }else {
                return ConflictSolveResult.UnSolved;
            }
        }
        if (c.lateralType == LateralType.Cross){
            double h1 = GeoUtil.calHeading(state1.location, state2.location);
            double h2 = GeoUtil.fixAngle(180 - h1);
            double a = GeoUtil.calIntersectionAngle(h1,state1.heading);
            double b = GeoUtil.calIntersectionAngle(180-h2,state2.heading);
            if(Math.abs(a) + Math.abs(b) > 180){  //交叉冲突已经开始相互远离
                return ConflictSolveResult.Solved;
            }else {
                return ConflictSolveResult.UnSolved;
            }
        }
        double h1 = GeoUtil.calHeading(state1.location,state2.location);
        AcftState s1 = state1;
        AcftState s2 = state2;
        if (Math.abs(GeoUtil.calIntersectionAngle(h1, state1.heading)) > 90){
            s1 = state2;
            s2 = state1;
        }
        if (s1.hSpdTAS < s2.hSpdTAS){  //同向不存在追赶
            return ConflictSolveResult.Solved;
        }else {
            return ConflictSolveResult.UnSolved;
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



















