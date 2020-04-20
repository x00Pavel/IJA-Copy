package Functional;


public class Coordinate{
    
    private  int y_cord;
    private  int x_cord;

    private Coordinate(int x, int y){
        this.x_cord = x;
        this.y_cord = y;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Coordinate){
            Coordinate cord = (Coordinate)o;
            if(this.hashCode() ==cord.hashCode()){
                return this.y_cord == cord.getY() && this.x_cord == cord.getX();
            }
            else{
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int res = 1;
        return  prime * res + this.x_cord + this.y_cord;

    }

    public int diffX(Coordinate c){
        return (int) Math.abs(this.x_cord - c.getX());
    }

    public  int diffY(Coordinate c){
        return (int) Math.abs(this.y_cord - c.getY());
    }

    public static Coordinate create(int x, int y){
        if(x < 0 || y < 0){
            return null;
        }
        Coordinate cord = new Coordinate(x, y);
        return cord;
    }

    public int getX(){
        return this.x_cord;
    }

    public int getY(){
        return this.y_cord;
    }

    public String toString(){
        return "["+this.x_cord+";"+this.y_cord+"]";
    }

}