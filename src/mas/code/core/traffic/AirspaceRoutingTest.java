package mas.code.core.traffic;

import mas.code.core.airspace.Waypoint;
import mas.code.core.framework.Scenario;
import mas.code.util.DataAccessObject;
import test.Demo1;

import java.util.List;

import static org.junit.Assert.*;

public class AirspaceRoutingTest {

    @org.junit.Test
    public void getPoints() {
        String path = "P197-UDINO-SATRO-DPX";
        Demo1 test = new Demo1();
        test.scenario = new Scenario();
        test.loadWayPoints();
//        System.out.println(test.scenario.waypoints.size());
        List<Waypoint> waypoints = test.makeWayPoints(path);
        Routing r = new Routing(path,waypoints);
//        AirspaceRouting base = new AirspaceRouting(waypoints);

//        for (int i = 0; i < base.getPoints().size();i++){
//            System.out.println(base.getPoints().get(i));
//        }
        for (int i = 0 ; i < r.getSegments().size();i++){
            System.out.println(r.getSegments().get(i) + " " + r.getSegments().get(i).getDistance());
        }
        System.out.println(r.getDistance());


    }

    @org.junit.Test
    public void getSegments() {
        DataAccessObject dao = new DataAccessObject();
        for (String pt:dao.allNaipPtMap.keySet()){
            System.out.println(pt + " " + dao.allNaipPtMap.get(pt)[1] + " " + dao.allNaipPtMap.get(pt)[0]);
        }



    }

    @org.junit.Test
    public void getDistance() {
    }





}