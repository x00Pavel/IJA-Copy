
package ija.functional;

/**
 * Reprezentuje pozici (souřadnice) v mapě. Souřadnice je dvojice (x,y), počátek mapy je vždy na pozici (0,0). 
 * Nelze mít pozici se zápornou souřadnicí.
 * @author koci
 */
public interface CoordinateInterface {

    /**
     * Vrací hodnotu souřadnice x.
     * @return Souřadnice x.
     */
    public int getX();
    
    /**
     * Vrací hodnotu souřadnice y.
     * @return Souřadnice y.
     */
    public int getY();
    
    public int diffX(Coordinate c);
    
    public int diffY(Coordinate c);
    
}
