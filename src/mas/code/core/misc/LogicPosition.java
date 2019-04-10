package mas.code.core.misc;

import com.mysql.jdbc.log.Log;
import mas.code.core.geom.Point2D;
import mas.code.util.GeoUtil;

import java.util.List;

public class LogicPosition {

    public BaseRouting routing;
    public int currentSegmentIndex = 0;

    public double distanceToTarget;
    public double headingToTarget;
    private double remainingSegmentsDistance;
    private double distanceToFinalPoint;
    private boolean onFinalSegment;

    public List<LogicSegment> segments;
    public LogicSegment currentSegment ;
    private LogicSegment firstSegment;
    private LogicSegment lastSegment;
    private LogicSegment nextSegment;
    private LogicSegment prevSegment;

    private Point2D target;

    public LogicPosition(BaseRouting routing, int currentSegmentIndex,double distanceToTarget, double headingToTarget){
        this.routing = routing;
        if(routing == null) {
            System.out.println("routing is null");
        }else{
//            System.out.println("!!!!!" + routing.getSegments().size());
        }
        this.currentSegmentIndex = currentSegmentIndex;
//        System.out.println("currentSegmentIndex" + currentSegmentIndex + " currentSegment" + getCurrentSegment());
        this.distanceToTarget = distanceToTarget;
        this.headingToTarget = headingToTarget;
        this.segments = routing.getSegments();
//        System.out.println("test : " + getSegmentSize());
        remainingSegmentsDistance = calcRemainingSegmentDistance();
        currentSegment = segments.get(currentSegmentIndex);
        target = getTarget();
//        System.out.println("target" + target);
    }

    public LogicPosition(BaseRouting routing, int currentSegmentIndex,Point2D location){
        this(routing,currentSegmentIndex,0.0,0.0);
        updateDistanceAndHeading(location);
//        System.out.println("LogicPosition Heading to Target" + this.headingToTarget);
    }

    public void setRouting(BaseRouting routing){
        this.routing = routing;
        currentSegmentIndex = 0;
    }
    public List<LogicSegment> getSegments(){
        return routing.getSegments();
    }
    public void setCurrentSegmentIndex(int currentSegmentIndex){
        this.currentSegmentIndex = currentSegmentIndex;
        this.currentSegment = routing.getSegments().get(currentSegmentIndex);

    }

    public int getSegmentSize(){
        return segments.size();
    }
    public LogicSegment getFirstSegment(){
        return routing.getSegments().get(0);
    }
    public LogicSegment getFLastSegment(){
        return routing.getSegments().get(getSegmentSize()-1);
    }
    public LogicSegment getNextSegment(){
        if(isOnFinalSegment()){
            return null;
        }else {
            return segments.get(currentSegmentIndex + 1);
        }
    }
    public LogicSegment getPrevSegment(){
        if(currentSegmentIndex <= 0 || getSegmentSize() <= 1){
            return null;
        }else {
            return segments.get(currentSegmentIndex - 1);
        }
    }
    public Point2D getTarget(){
        if (getCurrentSegment().direction.equals(Direction.Forward)) {  //这里需要检查，到底是不是这个意思；
            return getCurrentSegment().segment.end;
        }else {
            return getCurrentSegment().segment.start;
        }
    }

    public LogicSegment getCurrentSegment(){
        return getSegments().get(currentSegmentIndex);
    }
    public double getDistanceToFinalPoint(){
        return remainingSegmentsDistance + distanceToTarget;
    }
    public boolean isOnFinalSegment(){
        return this.currentSegmentIndex >= getSegmentSize()-1;
    }



    public void updateDistanceAndHeading(Point2D location){
        this.distanceToTarget = GeoUtil.calDist(location,target);
        this.headingToTarget = GeoUtil.calHeading(location,target);
        this.distanceToFinalPoint = this.distanceToTarget + remainingSegmentsDistance;
    }
    public void updateRoutingAndLocation(BaseRouting r, Point2D l){
        this.routing = r;
        this.updateDistanceAndHeading(l);
    }

    private double calcRemainingSegmentDistance(){
        double res = 0;
        for (int i = this.currentSegmentIndex+1; i < getSegmentSize(); i++){
            res += this.routing.getSegments().get(i).getDistance();
        }
        return res;
    }




}
