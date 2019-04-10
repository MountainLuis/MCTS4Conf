package test.detector;

 import kt.mas.core.traffic.aircraft.AircraftType;
 import mas.code.core.airspace.Waypoint;
 import mas.code.core.geom.Point2D;
 import mas.code.core.misc.BaseSegment;
 import mas.code.core.misc.Direction;
 import mas.code.core.misc.LogicPosition;
 import mas.code.core.misc.LogicSegment;
 import mas.code.core.traffic.Routing;
 import mas.code.core.traffic.aircraft.AcftState;
import mas.code.core.traffic.aircraft.Aircraft;
 import mas.code.util.DataAccessObject;
 import test.Demo1;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Random;

public class AcftGenerator {
    static Random rand = new Random(47);
    static DataAccessObject dao = new DataAccessObject();
    static HashMap<String, Waypoint> waypointsAll = new HashMap<>();


    public static AcftState AcftGenerator(){
        loadPoints();
        Aircraft aircraft = new Aircraft(RandomCallsignGenerator(),AircraftType.Companion.getALL().get("A320"));
        AcftState state = new AcftState(aircraft);
       double[] coord = RandomCoordGenerator();
       Point2D location = new Point2D(coord[0],coord[1]);
       state.location = location;
       state.alt = RandomAltGenerator();
       state.time = 250;
       state.finished = false;
       state.pos = getPosition();
        return state;
    }

    public static String RandomCallsignGenerator(){
        return AirLines.values()[rand.nextInt(7)] + "-" + String.valueOf(1200 + rand.nextInt(100));
    }

    public static double[] RandomCoordGenerator(){
        double lng = 112 + 6 * rand.nextDouble();
        double lat = 28 + 4 *rand.nextDouble();
        return new double[]{lng,lat};
    }
    public static double RandomAltGenerator(){
        return 6000 + 300 * rand.nextInt(11);
    }
    public static double RandomDirectGenerator(){
        return 5 * rand.nextInt(73);
    }

    public static LogicPosition getPosition(){
        String s = "P197-UDINO-SATRO-DPX";
        Routing r = makeRouting(s);
        return new LogicPosition(r,0,0,0);
    }
    public static void loadPoints(){
        for (String pt:dao.allNaipPtMap.keySet()){
            waypointsAll.put(pt,new Waypoint(pt,dao.allNaipPtMap.get(pt)[1],dao.allNaipPtMap.get(pt)[0]));
        }
    }
    public static Routing makeRouting(String l){
        String[] ss = l.split("-");
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        for (String s: ss){
            if (s != null) {
                Waypoint p = waypointsAll.get(s);
//                System.out.println(p);
                waypoints.add(p);
            }
        }
        Routing ret = new Routing(l,waypoints);
//        System.out.println(ret.getSegments().size() + " segment size");
        return ret;
    }

}

enum AirLines{
    CCA,CES,CSN,CSH,CXA,CHH,CSC
}