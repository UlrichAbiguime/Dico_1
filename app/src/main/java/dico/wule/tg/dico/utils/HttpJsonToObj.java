package dico.wule.tg.dico.utils;

import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import dico.wule.tg.dico.entities.DictResult;
import dico.wule.tg.dico.entities.Item;
import dico.wule.tg.dico.entities.Parts_Array_Item;
import dico.wule.tg.dico.entities.PinYinItem;
import dico.wule.tg.dico.entities.RetData;
import dico.wule.tg.dico.entities.Symbols_Array_Item;

import static dico.wule.tg.dico.utils.UtilsFunctions.trimAll;

/**
 * Created by lakiu_000 on 3/27/2015.
 */
public class HttpJsonToObj {

    public static Item fromReqtoObj (String from, String to, String data) throws Exception {

        data = data.trim();
//        data = data.replace(" ", "%20");
        data = URLEncoder.encode(data, "utf-8");
        String link = "http://apistore.baidu.com/microservice/dictionary?query="+data+"&from="+from+"&to="+to;
        Gson gson = new Gson();
        String jsonObj =  getPageAsString(link);
        Constants.makeLog (link);
        Constants.makeLog (jsonObj);
        boolean exception = false;
        Item item = null;
        try {
            item = gson.fromJson(jsonObj, Item.class);
            trimItem (item);
        } catch (Exception e) {
            e.printStackTrace();
            exception = true;
        }
//        Constants.makeLog (item.toString());
        if (exception == false)
            return item;

        if (true) {
            // on relace la requete pour le dict.
//            Constants. nbv(" ---------------------------------------------- \n    dictionnary gives nothing");
            String link2 = "http://apistore.baidu.com/microservice/translate?query=\'"+data+"\'&from="+from+"&to="+to;
            String jsonObj2 =  getPageAsString(link2);
            try {
                Item item2 = gson.fromJson(jsonObj2, Item.class);
                trimItem (item2);
                Constants.makeLog (link2);
                Constants.makeLog(jsonObj2);
                item = item2;
            } catch (Exception e) {
                e.printStackTrace();
                exception = true;
            }
        }

        if (exception == false)
            return item;

        // get off the quotes
        // if we have many results we may have also many arrays, so keep doing the things big for the time
        if (true) {
            String s = item.retData.trans_result[0].dst;
            s = trimAll (s);
            item.retData.trans_result[0].dst = s;
            Constants.makeLog("trimming ------------- ");
        }
        Constants.makeLog ("resultat: "+item);
        return item;
    }

    private static Item trimItem(Item item) {
        if (item != null) {
            if (item.retData.dict_result != null) {
                DictResult dictResult = item.retData.dict_result;
                dictResult.word_name = trimAll(dictResult.word_name);
                for (int i = 0; i < dictResult.symbols.length; i++) {
                    Symbols_Array_Item symb = dictResult.symbols[i];
                    for (int j = 0; j < symb.parts.length; j++) {
                        Parts_Array_Item part = symb.parts[j];
                        part.part = trimAll(part.part);
                        for (int k = 0; k < part.means.length; k++) {
                            part.means[k] = trimAll(part.means[k]);
                        }
                        part = symb.parts[j];
                    }
                    dictResult.symbols[i] = symb;
                }
            }
            if (item.retData.trans_result != null) {
                for (int i = 0; i < item.retData.trans_result.length; i++) {
                    ((RetData.LittleElem)((item.retData.trans_result)[i])).dst = trimAll(((RetData.LittleElem)((item.retData.trans_result)[i])).dst);
                }
            }
        }
        return item;
    }

    public static String getPageAsString(String home_path) throws Exception{

        String content = "";

        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();

        URL url = new URL(home_path);
        HttpURLConnection httpurlconnection = (HttpURLConnection) url
                .openConnection();
        httpurlconnection.setRequestProperty("Content-type", "text/html");
        httpurlconnection.setRequestProperty("Accept-Charset", "utf-8");
        httpurlconnection.setRequestProperty("contentType", "utf-8");
        // 填入apikey到HTTP header
        httpurlconnection.setRequestProperty("apikey",  "89c18ee4589deb2c808d2946ea80ad2c");
        httpurlconnection.connect();
        InputStream is = httpurlconnection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String strRead = null;
        while ((strRead = reader.readLine()) != null) {
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        reader.close();
        content = sbf.toString();
        return content;
    }


    public static String trim(char c, String s) {

        int start = 0, last =  s.length() - 1;
        int end = last;
        char[] value = s.toCharArray();
        while ((start <= end) && (value[start] == c)) {
            start++;
        }
        while ((end >= start) && (value[end] == c)) {
            Constants.makeLog("position "+end+" char is ..."+value[end]+ " ...");
            end--;
        }
        if (start == 0 && end == last) {
            return s;
        }
        return s.substring(start, end+1);
    }

    public static String getPinyinOfStr(String text) {

        String content = "";
        String home_path = "http://string2pinyin.sinaapp.com/?str="+text;
//        home_path = "http://string2pinyin.sinaapp.com/?str={%22symbols%22:[{%22parts%22:[{%22means%22:[%22%E4%B8%A5%E8%82%83%E7%9A%84%EF%BC%8C%E4%B8%A5%E9%87%8D%E7%9A%84%22,%22%E8%AE%A4%E7%9C%9F%E7%9A%84%EF%BC%8C%E5%BA%84%E9%87%8D%E7%9A%84%22,%22%E9%87%8D%E8%A6%81%E7%9A%84%22,%22%E5%8D%B1%E9%99%A9%E7%9A%84%22],%22part%22:%22adj.%22}],%22ph_am%22:%22%CB%88s%C9%AAri%C9%99s%22,%22ph_en%22:%22%CB%88s%C9%AA%C9%99ri%C9%99s%22}],%22word_name%22:%22serious%22}";
        home_path = trimAll(home_path);
//        home_path = home_path.replace(" ", "%20");
//        home_path = home_path.replace("\"", "%22");
//        home_path = home_path.replace("}", "%125");
//        home_path = home_path.replace("{", "%123");

        try {
            HttpGet req = new HttpGet(home_path);
            HttpResponse resp = new DefaultHttpClient().execute(req);
            req.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    content+=inputLine;
                Constants.makeLog(home_path);
                content = trimAll(content);
                PinYinItem pinYinItem = null;
                Constants.makeLog("VVVVVVVVVVVV "+content);
                if (content != "") {
                    Constants.makeLog(content);
                    pinYinItem = (new Gson()).fromJson(content, PinYinItem.class);
                    return pinYinItem.pinyin;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}
