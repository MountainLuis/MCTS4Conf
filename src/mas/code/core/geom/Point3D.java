package mas.code.core.geom;

public class Point3D extends Point2D {
    double alt;
    public Point3D(){}
    public Point3D(double lng, double lat,double alt){
        super(lng,lat);
        this.alt = alt;
    }
    @Override
    public int hashCode(){
        long altBits = Double.doubleToLongBits(alt);
        altBits = altBits ^ altBits>>>32;
        return super.hashCode()*31 + (int)altBits;
    }
    @Override
    public boolean equals(Object o){
        if (o instanceof Point3D){
            Point3D obj = (Point3D)o;
            if(this.hashCode() == o.hashCode()){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

}
