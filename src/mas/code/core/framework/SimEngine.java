package mas.code.core.framework;

import mas.code.core.traffic.ATC.ATCController;
import mas.code.core.traffic.aircraft.Aircraft;

public class SimEngine {
    Scenario scenario;
    public  IScenarioBuilder scenarioBuilder;
    private void preSim(){
        scenarioBuilder.build();
        scenario.clock.setStartTime(-1 * scenario.clock.getTimeStep());
    }
    public void sim(){
        Aircraft acft0 = scenario.aircraft0;
        Aircraft acft1 = scenario.aircraft1;
        ATCController ctrl = scenario.atcController;
        int i = 0;
        while(true){
            scenario.clock.doStep();
            acft0.doStep();
            acft1.doStep();
            ctrl.doStep();
            if(acft0.isFinished() || acft1.isFinished()){
//                System.out.println(acft0.curState.alt + " ," + acft1.curState.alt);
                break;
            }
//            if (i++ == 2000){//acft0.isFinished()){
//                break;
//            }
        }

    }
    private void postSim(){

    }

    public void run(){
        preSim();
        sim();
        postSim();
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }
}
