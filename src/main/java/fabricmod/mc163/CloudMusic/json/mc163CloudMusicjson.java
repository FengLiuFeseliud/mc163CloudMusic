package fabricmod.mc163.CloudMusic.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class mc163CloudMusicjson {

    public static class JsonData{
        public int volume; //默认音量
        public int pageRow; //推荐歌单/新音乐 一页的打印行数
        public Cache Cache; //缓存相关
        public Cookie Cookie; //Cookie
        public MusicList MusicList;//需要收藏歌的歌单
    }

    public static class Cookie{
        public boolean CookieRead; //是否读取Cookie 默认否
        public String CookieData; //Cookie数据
    }

    public static class MusicList{
        public String title; //歌单标题
        public String id; //歌单id
    }

    public static class Cache{
        public String downloadPath; //默认缓存路径
        public String CacheSize; //缓存大小
    }

    public static String jsonFormat(String JSONString){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser JsonParser = new JsonParser();
        JsonElement JsonElement = JsonParser.parse(JSONString);
        return gson.toJson(JsonElement);
    }

    public static void Set() throws IOException {
        File file = new File("");
        String Path = file.getCanonicalPath() + "\\mods\\";
        String downloadPath = Path + "CloudMusic";
        File jsonfile = new File(Path + "mc163CloudMusic.json");
        if (!jsonfile.exists()) {
            jsonfile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(jsonfile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            Gson gson = new GsonBuilder().create();
            //Cookie
            Cookie Cookie = new Cookie();
            Cookie.CookieRead = false;
            Cookie.CookieData = "";
            //MusicList
            MusicList MusicList = new MusicList();
            MusicList.title = "";
            MusicList.id = "";
            //Cache
            Cache Cache = new Cache();
            Cache.downloadPath = downloadPath;
            Cache.CacheSize = "1";
            //创建json
            JsonData JsonData = new JsonData();
            JsonData.volume =-33;
            JsonData.pageRow =5;
            JsonData.Cookie = Cookie;
            JsonData.MusicList = MusicList;
            JsonData.Cache = Cache;
            //格式化json字符串
            String jsonString = jsonFormat(gson.toJson(JsonData));

            bufferedWriter.write(jsonString);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }

    public static mc163CloudMusic get() throws IOException{
        String Path = new File("").getCanonicalPath() + "\\mods\\";
        String Json= FileUtils.readFileToString(new File(Path + "mc163CloudMusic.json"),"UTF-8");

        //解析json字符串
        return new Gson().fromJson(Json, mc163CloudMusic.class);
    }
}

