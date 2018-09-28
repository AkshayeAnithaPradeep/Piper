package songExtractor;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Akshaye AP on 27-04-2017.
 */

public class JSONExtractor {

    public HashMap<String,String> extract(String jsonin) throws JSONException {
        HashMap<String, String> output=new HashMap<String, String>();
        JSONObject jsonObject=new JSONObject(jsonin);
        JSONObject jsonObject2=jsonObject.getJSONObject("metadata");
        JSONArray jsonArray=jsonObject2.getJSONArray("music");
        JSONObject jsonObject3=jsonArray.getJSONObject(0);
        JSONArray jsonArray2=jsonObject3.getJSONArray("artists");
        JSONObject title=jsonArray2.getJSONObject(0);
        output.put("track",jsonObject3.getString("title"));
        output.put("artist",title.getString("name"));

        /*JSONArray jsonArray3=jsonObject3.getJSONArray("genres");
        String genres="";
        for (int i = 0; i < jsonArray3.length(); i++) {
            JSONObject tempObj=jsonArray3.getJSONObject(i);
            genres+=tempObj.getString("name");
        }
        output.put("genres",genres.toUpperCase());*/
        return output;

    }
}
