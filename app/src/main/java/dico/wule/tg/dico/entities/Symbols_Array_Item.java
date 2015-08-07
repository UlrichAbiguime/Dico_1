package dico.wule.tg.dico.entities;

import java.util.Arrays;

/**
 * Created by lakiu_000 on 5/5/2015.
 */
public class Symbols_Array_Item {



    public String ph_am, ph_en;
    public Parts_Array_Item[] parts;

    @Override
    public String toString() {
        return "Symbols_Array_Item{" +
                "ph_am='" + ph_am + '\'' +
                ", ph_en='" + ph_en + '\'' +
                ", parts=" + Arrays.toString(parts) +
                '}';
    }
}
