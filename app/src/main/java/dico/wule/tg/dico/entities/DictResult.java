package dico.wule.tg.dico.entities;

import java.util.Arrays;

/**
 * Created by lakiu_000 on 5/5/2015.
 */
public class DictResult {


    public String word_name;
    public Symbols_Array_Item[] symbols;

    @Override
    public String toString() {
        return "DictResult{" +
                "word_name='" + word_name + '\'' +
                ", symbols=" + Arrays.toString(symbols) +
                '}';
    }
}
