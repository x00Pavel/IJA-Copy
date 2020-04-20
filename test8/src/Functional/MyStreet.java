package Functional;

import Functional.Street;

import java.util.List;

/**
 * Reprezentuje jednu ulici v mapě. Ulice má svůj identifikátor (název) a je definována souřadnicemi. Pro 1. úkol
 * předpokládejte pouze souřadnice začátku a konce ulice.
 * Na ulici se mohou nacházet zastávky.
 * @author koci
 */
public interface MyStreet {
    /**
     * @return Vrací souřadnice konce ulice.
     */
    public Coordinate end();

    /**
     * @return Vrací souřadnice začátku ulice.
     */
    public Coordinate begin();
    
    /**
     * Vytvoří ulici (instance implicitní implementace).
     * 
     * @param id
     * @param coordinates list of coordinates that represents
     */
    public static Street defaultStreet(String id, Coordinate... coordinates){
        return Street.defaultStreet(id, coordinates);
    }
    public boolean follows(Street s);
    /**
     * Vrátí identifikátor ulice.
     * @return Identifikátor ulice.
     */
    public String getId();
    
    /**
     * Vrátí seznam souřadnic definujících ulici. První v seznamu je vždy počátek a poslední v seznamu konec ulice.
     * @return Seznam souřadnic ulice.
     */
    
    public List<Coordinate> getCoordinates();
    
    /**
     * Vrátí seznam zastávek na ulici.
     * @return Seznam zastávek na ulici. Pokud ulize nemá žádnou zastávku, je seznam prázdný.
     */
    public List<Stop> getStops();
    
    /**
     * Přidá do seznamu zastávek novou zastávku.
     * @param stop Nově přidávaná zastávka.
     */
    public boolean addStop(Stop stop);
}
