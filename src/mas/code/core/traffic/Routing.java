package mas.code.core.traffic;

import mas.code.core.airspace.Waypoint;
import mas.code.core.geom.Point2D;
import mas.code.core.misc.LogicSegment;

import java.util.List;

public class Routing extends AirspaceRouting{
    public String id;

//    public Routing(List<Waypoint> points) {
//        super(points);
//    }
    public Routing(String id,List<Waypoint> points){
        super(points);
        this.id = id;
    }
    public String toString(){
//        StringBuffer sb = new StringBuffer();
//        for (Waypoint p : points){
//            sb.append(p.id);
//            sb.append("::");
//        }
        return id;

    }

}
