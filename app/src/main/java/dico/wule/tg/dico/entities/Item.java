package dico.wule.tg.dico.entities;

/**
 * Created by lakiu_000 on 3/27/2015.
 */
public class Item {

    /*{
    {"errNum":0,"errMsg":"success","retData":{"from":"en","to":"zh","dict_result":[]}}
}*/
    public int errNum;
    public String errMsg;
    public RetData retData;

    public Item(Item item) {
        errNum = item.errNum;
        errMsg = item.errMsg;
        retData = item.retData;
    }

    public Item() {
        errNum = 0;
        errMsg = "";
        retData = new RetData();
    }


    @Override
    public String toString() {
        return "Item{" +
                "errNum=" + errNum +
                ", errMsg='" + errMsg + '\'' +
                ", retData=" + retData +
                '}';
    }
}
