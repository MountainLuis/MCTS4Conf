package mas.code.core.geom;

public class Point2D {
    public double lng;
    public double lat;

    public Point2D(){}
    public Point2D(double lng, double lat){
        this.lng = lng;
        this.lat = lat;
    }
    public Point2D(Point2D point){
        this.lat = point.lat;
        this.lng = point.lng;
    }
    @Override
    public int hashCode(){
       long lngBits = Double.doubleToLongBits(lng);
       long latBits = Double.doubleToLongBits(lat);
        lngBits = lngBits ^ lngBits>>>32;
        latBits = latBits ^ latBits>>>32;
        return (int)lngBits*31 + (int)latBits;
    }
    @Override
    public boolean equals(Object o){
        if (o instanceof Point2D){
            Point2D obj = (Point2D)o;
            if(this.hashCode() == obj.hashCode()){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
    public String toString(){
        return "(" + lng + "," + lat + ")";
    }
}
