package mas.code.core.framework;

import mas.code.core.airspace.Segment;
import mas.code.core.airspace.Waypoint;
import mas.code.core.traffic.ATC.ATCController;
import mas.code.core.traffic.aircraft.Aircraft;

import java.util.HashMap;

public class Scenario {

    public static SimpleClock clock = new SimpleClock();;
    public  static Aircraft aircraft0 ;
    public  static Aircraft aircraft1;
    public ATCController atcController;
    public  HashMap<String, Waypoint> waypoints = new HashMap<>();
    public  static HashMap<String, Segment> segments = new HashMap<>();

    public Scenario(){
        clock = new SimpleClock();
        atcController = new ATCController();
        waypoints = new HashMap<>();
        segments = new HashMap<>();
    }


}
