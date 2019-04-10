package mas.code.core.airspace;

import mas.code.core.geom.Point2D;

public class Waypoint extends Point2D {

    public String id;
    public double lng;
    public double lat;

    public Waypoint(String id, double lng, double lat) {
        super(lng, lat);
        this.id = id;
    }

    public int hashCode(){
        return super.hashCode() * 31 + id.hashCode();
    }

    public boolean equals(Object o){
        if (o instanceof Waypoint){
            Waypoint wp = (Waypoint)o;
            if (this.hashCode() == wp.hashCode()){
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }
}
