package mas.code.core.traffic;

import mas.code.core.airspace.Segment;
import mas.code.core.airspace.Waypoint;
import mas.code.core.framework.Scenario;
import mas.code.core.misc.BaseRouting;
import mas.code.core.misc.Direction;
import mas.code.core.misc.LogicSegment;

import java.util.ArrayList;
import java.util.List;

public class AirspaceRouting implements BaseRouting {


    private List<Waypoint> points;
    private List<LogicSegment> segments;
    private double distance;
    public AirspaceRouting(List<Waypoint> points){
        this.points = points;
        this.segments = buildSegments();
        this.distance = calcDistance();
    }

    private double calcDistance(){
        double res = 0;
        for (int i = 0; i < segments.size(); i++){
            res += segments.get(i).getDistance();
        }
        return res;
    }

    private List<LogicSegment> buildSegments(){
//        System.out.println("HERE");
     List<LogicSegment> res = new ArrayList<>();
        Waypoint lastPt = null;
        for (Waypoint pt0 : points){
            if(lastPt != null) {
                Segment seg = getSegment(lastPt, pt0);
//                System.out.println("First Seg:" + seg.id);
                if(lastPt.equals(seg.start)) {
                    LogicSegment lSeg = new LogicSegment(seg,Direction.Forward);
                    res.add(lSeg);
//                    System.out.println("Seg:" + lSeg);
                }else {
                    LogicSegment lSeg = new LogicSegment(seg,Direction.Backward);
                    res.add(lSeg);
//                    System.out.println("Seg:" + lSeg);
                }
            }
            lastPt = pt0;
        }
        return res;
    }
    private Segment getSegment(Waypoint p0, Waypoint p1){
        String segID;
        Waypoint start,end;
        if(p0.id.compareTo(p1.id) > 0) {
            start = p0;
            end = p1;
        }else{
            start = p1;
            end = p0;
        }
        segID = "SEG#" + start.id + "_" + end.id;
//        System.out.println(start + " " + end);
        if(start == null || end == null) {
            System.out.println("Check point ");
        }
        return Scenario.segments.computeIfAbsent(segID,k ->new Segment(segID,start,end));
    }



    @Override
    public List<Waypoint> getPoints() {
        return points;
    }

    @Override
    public List<LogicSegment> getSegments() {
        return segments;
    }

    @Override
    public double getDistance() {
        return distance;
    }

}
