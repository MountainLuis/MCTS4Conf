package mas.code.core.misc;

import mas.code.core.airspace.Waypoint;
import mas.code.core.geom.Point2D;
import mas.code.util.GeoUtil;

import java.util.ArrayList;
import java.util.List;

public class SimpleRouting implements BaseRouting {
    public Point2D start;
    public Point2D end;
    List<Point2D> points = null;
   List<LogicSegment> segments = null;
   double distance;

    public SimpleRouting(Point2D start, Point2D end){
        this.start = start; this.end = end;
        points.add(start);
        points.add(end);
        segments = makeSegment();
        distance = GeoUtil.calDist(start,end);
    }
    private List<LogicSegment> makeSegment(){
        BaseSegment seg = new BaseSegment(start,end);
        LogicSegment lseg = new LogicSegment(seg,Direction.Forward);
        List<LogicSegment> res = new ArrayList<>();
        res.add(lseg);
        return res;
    }
    @Override
    public List<Point2D> getPoints() {
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
