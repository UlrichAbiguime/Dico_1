package dico.wule.tg.dico.entities;

/**
 * Created by lakiu_000 on 5/22/2015.
 */
public class PinYinItem {

/*

{
    "status": "T",
    "str": "中文is good",
    "pinyin": "zhōng wén is good",
    "setting": {
        "accent": 1,
        "delimiter": " ",
        "traditional": 0,
        "letter": 0,
        "only_chinese": 0
    },
    "doc": "http://string2pinyin.sinaapp.com/doc.html"
}
  * */

    public String status, str, pinyin, doc;
    public MySettings setting;


    private class MySettings {

        public int accent, traditional, letter, only_chinese;
public String  delimiter;

        @Override
        public String toString() {
            return "MySettings{" +
                    "accent=" + accent +
                    ", delimiter=" + delimiter +
                    ", traditional=" + traditional +
                    ", letter=" + letter +
                    ", only_chinese=" + only_chinese +
                    '}';
        }
    }
    @Override
    public String toString() {
        return "PinYinItem{" +
                "status='" + status + '\'' +
                ", str='" + str + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", doc='" + doc + '\'' +
                ", setting=" + setting +
                '}';
    }
}
