package dico.wule.tg.dico.entities;

import java.util.Arrays;

/**
 * Created by lakiu_000 on 5/5/2015.
 */
public class Parts_Array_Item {

    public String part;
    public String[] means;

    @Override
    public String toString() {
        return "Parts_Array_Item{" +
                "part='" + part + '\'' +
                ", means=" + Arrays.toString(means) +
                '}';
    }
}
