package dico.wule.tg.dico.entities;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by lakiu_000 on 3/27/2015.
 */
public class RetData {
    /*
*  "from": "en",
        "to": "zh",
        "trans_result": [
            {
                "src": "\"i am good\"",
                "dst": "“我很好”"
            }
        ]
* */
    public  String from, to;
    public LittleElem[] trans_result;
    public DictResult dict_result;


    @Override
    public String toString() {
        return "RetData{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", trans_result=" + Arrays.toString(trans_result) +
                ", dict_result=" + dict_result +
                '}';
    }

    public class LittleElem {
        public String src, dst;

        @Override
        public String toString() {
            return "LittleElem{" +
                    "src='" + src + '\'' +
                    ", dst='" + dst + '\'' +
                    '}';
        }
    }
}
