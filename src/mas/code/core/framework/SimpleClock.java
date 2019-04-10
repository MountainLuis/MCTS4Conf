package mas.code.core.framework;

public class SimpleClock implements Clock {

     private int startTime = 0;
     private int time;
     private int timeStep;

     public SimpleClock(){
         this.startTime = 0;
         timeStep = Config.DEFAULT_TIMESTEP;
     }

     public void setStartTime(int startTime){
         this.startTime = startTime;
         time = startTime;
         System.out.println("Start Time : " + startTime);
     }
    @Override
    public int getStartTime() {
        return startTime;
    }

    @Override
    public int getTimeStep() {
        return timeStep;
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public void doStep() {
          time += timeStep;
        System.out.println("Time " + time);
    }
}
