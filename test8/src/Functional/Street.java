/*
 * Zdrojové kódy josu součástí zadání 1. úkolu pro předmětu IJA v ak. roce 2019/2020.
 * (C) Radek Kočí
 */
package Functional;

import java.util.AbstractMap;
import java.util.List;

/**
 * Reprezentuje jednu ulici v mapě. Ulice má svůj identifikátor (název) a je definována souřadnicemi. Pro 1. úkol
 * předpokládejte pouze souřadnice začátku a konce ulice.
 * Na ulici se mohou nacházet zastávky.
 * @author koci
 */
public interface Street {
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
    
    public static Street defaultStreet(String string, Coordinate ... coordinates) {
		Street street = new MyStreet(string, coordinates);
		
//		for (int i = 0; i < coordinates.length - 2; i++) {
//			Coordinate start = coordinates[i];
//			Coordinate end = coordinates[i+2];
//			Coordinate midl = coordinates[i+1];
//			Coordinate vektor1 = Coordinate.create(midl.getX()-start.getX(), midl.getY()-start.getY());
//			Coordinate vektor2 = Coordinate.create(end.getX()-midl.getX(), end.getY()-midl.getY());
//			if (vektor1.getX()*vektor2.getX()+vektor1.getY()*vektor2.getY() != 0) {
//				return null;
//			}
//		}
		return street;
	}

	public Object begin();

	public Object end();

	public boolean follows(Street s);

	public List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStopLocation();
}
