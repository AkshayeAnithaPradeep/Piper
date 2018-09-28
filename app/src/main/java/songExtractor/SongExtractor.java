package songExtractor;

/**
 * Created by Akshaye AP on 21-02-2017.
 */

import android.net.Uri;
import android.util.Log;

import com.acrcloud.utils.ACRCloudRecognizer;
import com.illuminati.akshayeap.audioconverter.ConverterThread;
import com.illuminati.akshayeap.feature_extraction.ExtractorThread;
import com.illuminati.akshayeap.piper.GenreFragment;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.track.MusicGenreList;
import org.jmusixmatch.entity.track.PrimaryGenres;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SongExtractor extends Thread{

    private Callback callback;
    private String mFileName;

    public SongExtractor(Callback callback, String filename) {

        this.callback=callback;
        mFileName=filename.replace("file://","");
        Uri uRi= Uri.parse(mFileName);
        mFileName=uRi.getPath();
    }


    public void convert() throws JSONException, IOException, MusixMatchException {

        HashMap<String,String> finalval=new HashMap<String,String>();


        Log.v("text1","Starting conversion for file: "+mFileName);
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("host", "identify-ap-southeast-1.acrcloud.com");
        config.put("access_key", "8fc1271d934925750eb310232ae67079");
        config.put("access_secret", "8QV1gtC8niKMbBtdVMwLhRr7ad3YzxDqjmHDbPIy");
        config.put("debug", false);
        config.put("timeout", 10); // seconds

        ACRCloudRecognizer re = new ACRCloudRecognizer(config);

        // It will skip 80 seconds.
        String result = re.recognizeByFile(mFileName, 0);
        System.out.println("result:"+result);
        JSONObject resultObj=new JSONObject(result);
        JSONObject tempObj=resultObj.getJSONObject("status");
        String msg=tempObj.getString("msg");
        if(!(msg.equals("Success"))) {
            finalval.put("status","fail");
            callback.oncallback(finalval);
            return;
        }
        JSONExtractor jsonExtractor=new JSONExtractor();
        HashMap<String,String> out=jsonExtractor.extract(result);
        System.out.println(out.get("track"));
        System.out.println(out.get("artist"));

        String trackName=out.get("track");
        String artistName =out.get("artist");

        String apiKey = "e21203d90015bceeb753d2d927f22d76";

        MusixMatch musixMatch = new MusixMatch(apiKey);

        Track track = musixMatch.getMatchingTrack(trackName, artistName);

        TrackData data = track.getTrack();

        Locale locale = Locale.getDefault();

        String urlString = "http://www.metrolyrics.com/"

                + removeNonAlphabeticalChars(trackName.toLowerCase(locale))
                + "-lyrics-" +removeNonAlphabeticalChars(artistName.toLowerCase(locale))
                + ".html";
        Document document=null;
        try {
            document=Jsoup.connect(urlString).get();
        } catch (HttpStatusException e) {
            System.out.println("No webpage found");
        }
        String lyricdata = "";
        if(document!=null) {
            Elements elements = document.getElementsByAttributeValueContaining("class", "lyrics-body");
            if (elements != null && elements.size() > 0) {

                for (Element element : elements) {
                    Elements pTags = element.getElementsByTag("p");
                    if (pTags != null && pTags.size() > 0) {
                        for (Element pTag : pTags) {
                            String dataLine = replaceBrWithNewLine(pTag.toString());
                            dataLine = removeHTMLTags(dataLine) + "\n";
                            lyricdata += dataLine;
                        }
                    }
                }

            }
        }
        SmithWaterman waterman=new SmithWaterman();
        PrimaryGenres genres=data.getPrimaryGenres();
        String genreList="";
        List<MusicGenreList> list=genres.getMusicGenreList();
        Iterator<MusicGenreList> genrelist=list.iterator();
        while(genrelist.hasNext())
        {
            genreList+=genrelist.next().getMusicGenre().getMusicGenreName();
        }

        System.out.println("Genre:"+genreList);

        finalval.put("genre",waterman.findGenre(genreList.toUpperCase()));
        finalval.put("lyrics",lyricdata);
        finalval.put("title", trackName);
        finalval.put("artist",artistName);
        finalval.put("status","success");

        callback.oncallback(finalval);

        ConverterThread converterThread = new ConverterThread(mFileName);
        converterThread.start();
        String wavfile = "new.wav";// args[0]+".wav";
        while (converterThread.isAlive()) {}
		Log.v("text2","Starting extraction");
        ExtractorThread extractorThread = new ExtractorThread(wavfile,null);
        extractorThread.start();
        while(extractorThread.isAlive()) {}

    }

    public static String removeNonAlphabeticalChars(String string) {

        return string.replaceAll("\\s","-")

                .replaceAll(" ","-")

                .replaceAll("[^a-zA-Z0-9\\s]", "-");

    }

    public static String  replaceBrWithNewLine(String string) {
        return string.replaceAll("(?i)<br[^>]*>","\n");
    }

    public static String removeHTMLTags(String string) {
        return  string.replaceAll("\\<.*?>","");
    }
}
