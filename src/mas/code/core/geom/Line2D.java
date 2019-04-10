package mas.code.core.geom;

import mas.code.util.GeoUtil;

public class Line2D {
    public Point2D start;
    public Point2D end;

    public double distance;
    public double heading;

    public Line2D(Point2D start, Point2D end){
        this.start = start;
        this.end = end;
        this.distance = GeoUtil.calDist(start.lng,start.lat,end.lng,end.lat);
        this.heading = GeoUtil.calHeading(start.lng,start.lat,end.lng,end.lat);
    }
    public int hashCode(){
        return start.hashCode() + end.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Line2D) {
            Line2D line = (Line2D)obj;
            if(line.start.equals(this.start) && line.end.equals(this.end)){
                return true;
            }
        }
        return false;
    }
}
