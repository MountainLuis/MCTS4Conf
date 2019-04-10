package test.detector;

import demo.spatialindex.rtree.SpatialData;
import mas.code.core.traffic.ATC.conflict.Conflict;
import mas.code.core.traffic.ATC.conflict.ConflictDetector;
import mas.code.core.traffic.aircraft.AcftState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConflictDetection {

    public static void main(String[] args) {
        ConflictDetection detection = new ConflictDetection();
        List<AcftState> states = detection.makePosition(1000);
        long rtStart = System.currentTimeMillis();
        List<Conflict> conflicts = ConflictDetector.conflictDetect(states);
        System.out.println(System.currentTimeMillis() - rtStart);
    }

    public List<AcftState> makePosition(int size){
        List<AcftState> res = new ArrayList<>();
        for (int i = 0; i < size; i++){
            res.add(AcftGenerator.AcftGenerator());
        }
        return res;
    }

}
