package mas.code.core.traffic.ATC;

public class AltCmd implements ATCCmd {

    public int assignTime;
    public double delta;
    public double targetAlt;

    public AltCmd(int assignTime,double delta,double targetAlt){
        this.assignTime = assignTime;
        this.delta = delta;
        this.targetAlt = targetAlt;
    }

    @Override
    public Type getType() {
        return Type.Altitude;
    }

    @Override
    public int getAssignTime() {
        return assignTime;
    }

    @Override
    public void setAssignTime(int time) {
        this.assignTime = time;
    }

    @Override
    public String toString(){
        return "AltCmd :[ time:" + assignTime + ",Delta:" + delta + ",Target:" + targetAlt + "]";
    }

}
