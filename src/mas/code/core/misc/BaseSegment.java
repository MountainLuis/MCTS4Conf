package mas.code.core.misc;

import mas.code.core.geom.Line2D;
import mas.code.core.geom.Point2D;

public class BaseSegment extends Line2D {
    public DirectionUsage usage;
    public BaseSegment(Point2D start, Point2D end){
        super(start, end);
    }
    public BaseSegment(Point2D start, Point2D end, DirectionUsage usage){
        super(start, end);
        this.usage = usage;
    }
    public String toString(){
        return "Start:("+start.lng+","+start.lat+"),End:("+end.lng+","+end.lat+")";
    }

    public int hashCode(){
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseSegment){
            BaseSegment bs = (BaseSegment)obj;
            if (bs.start.equals(this.start) && bs.end.equals(this.end))
             return  true;
        }
        return false;
    }
}
