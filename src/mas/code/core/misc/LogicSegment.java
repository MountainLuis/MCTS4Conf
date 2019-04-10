package mas.code.core.misc;

import mas.code.core.geom.Point2D;
import mas.code.util.GeoUtil;

public class LogicSegment {

    public BaseSegment segment;
    public Direction direction ;

    public LogicSegment(BaseSegment segment, Direction direction){
        this.segment = segment;
        this.direction = direction;
    }

    public double getHeading(){
        if (direction.equals(Direction.Forward)){
            return segment.heading;
        }else {
            return GeoUtil.fixAngle(segment.heading - 180);
        }
    }
    public double getDistance(){return segment.distance;}
    public Point2D getStart(){
        if (direction.equals(Direction.Forward)){
            return segment.start;
        }else{
            return segment.end;
        }
    }
    public Point2D getEnd(){
        if (direction.equals(Direction.Forward)){
            return segment.end;
        }else{
            return segment.start;
        }
    }
    public String toString(){
        return segment.toString() +" direction:"+ direction;
    }

    public int hashCode(){
        return this.segment.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LogicSegment){
            LogicSegment ls = (LogicSegment)obj;
            if (ls.segment.equals(this.segment) && ls.direction.equals(this.direction)){
                return true;
            }
        }
        return false;
    }
}
