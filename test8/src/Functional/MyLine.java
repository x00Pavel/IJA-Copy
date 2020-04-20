package Functional;

import java.util.List;

import Functional.Line;
import Functional.Stop;
import Functional.Street;

import java.util.AbstractMap; //<Street,​Stop>>

public interface MyLine{
    /**
     * Insert stop to the transport line
     * 
     * @param stop
     * @return True if insertion is successful, otherwise false
     */
    public boolean addStop(Stop stop);

    /**
     * Add street to transport line
     * @param street
     * @return Boolean value true if insertion is successful, otherwise false
     */
    public boolean addStreet(Street street);

    public static Line defaultLine(String id){
        return Line.defaultLine(id);
    }

    
    public List<AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute();
}