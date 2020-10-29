package fabricmod.mc163.CloudMusic;

import com.google.gson.Gson;
import fabricmod.mc163.CloudMusic.Play.Cache;
import fabricmod.mc163.CloudMusic.json.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import java.io.*;
import java.net.*;
import java.util.Objects;

public class Http{

    public static class MusicRowException extends Exception{
        public MusicRowException(String message){
            super(message);
        }
    }

    public static String HttpAPIPOST(String path,String Data){
        String Json = null;
        try {
            URL url = new URL(path);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("POST");

            OutputStream OutputStream = httpConn.getOutputStream();
            OutputStream.write(Data.getBytes());
            if (httpConn.getResponseCode() == 200) {
                InputStream is = httpConn.getInputStream();
                //内存输出流
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len = 0;
                while((len = is.read(buf))!=-1){
                    baos.write(buf, 0, len);
                }
                byte[] byteData = baos.toByteArray();
                Json =new String(byteData);
                is.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Json;
    }

    public static String getCookie(String CookieData, ServerCommandSource source){
        String Cookie = null;
        if (CookieData != null){
            source.sendFeedback(new LiteralText("music163:使用配置文件中的Cookie获取"), true);
            Cookie =CookieData;
        } else {
            source.sendFeedback(new LiteralText("music163:使用配置文件中的Cookie获取,可并未填写Cookie"), true);
        }
        return Cookie;
    }

    public static String[] GetMusic(String  type,int musicRow,String[] musicIDList) throws MusicRowException {
        if ( musicRow-1 < 0 || musicRow > musicIDList.length){
            throw new MusicRowException("musicRow不能小于0或大于歌单单曲数");
        }
        if (Objects.equals(type, "CachePlay")){

        }
        String path = "https://api.feseliud.com/music163/getmusic.php";
        String JsonData = HttpAPIPOST(path,"id=" + musicIDList[musicRow-1]);
        //解析json字符串
        Gson gson = new Gson();
        MusicJsonToMusicDataList json = gson.fromJson(JsonData, MusicJsonToMusicDataList.class);
        //将数据存入数组
        String[] musicData = {"","","","","",""};
        if (json.getCode().equals("0")){
            musicData[0] = json.getTitle(); //标题
            musicData[1] = json.getSubtitle(); //单曲副标题
            musicData[2] = json.getSinger(); //单曲歌手
            musicData[3] = json.getAlbum();//单曲专辑
            musicData[4] = json.getPic();//单曲封面Url
            musicData[5] = musicIDList[musicRow-1];//单曲id
        }
        return musicData;
    }

    public static String[][] GetMusicList(String musicListID){
        String path ="https://api.feseliud.com/music163/getmusiclist.php";
        String JsonData = HttpAPIPOST(path,"id=" + musicListID);
        //解析json字符串
        Gson gson = new Gson();
        MusicListJsonToMusicList json = gson.fromJson(JsonData, MusicListJsonToMusicList.class);
        //将数据存入数组
        String[][] musicListData= {{"","","","",""},{}};
        if (json.getCode().equals("0")){
            musicListData[0][0] = json.getTitle(); //标题
            musicListData[0][1] = json.getCreator(); //作者
            musicListData[0][2] = json.getCreationdate(); //创建日期
            musicListData[1]  = json.getPlaylist().split(",");//歌单全部单曲id;
            musicListData[0][3] = String.valueOf(musicListData[1].length);//单曲数
            musicListData[0][4] = json.getPlaycount();//播放数
        }

        return  musicListData;
    }

    public static String[][] GetDailyMusicList(String Cookie){
        String path ="https://api.feseliud.com/music163/getdailymusiclist.php";
        String JsonData = HttpAPIPOST(path,"Cookie=" + Cookie);
        //解析json字符串
        Gson gson = new Gson();
        DailyMusicList json = gson.fromJson(JsonData, DailyMusicList.class);
        //将数据存入数组
        String[][] musicListData= {{},{}};
        if (json.getCode().equals("0")){
            musicListData[0]  = json.getPlaylist().split(",");//歌单全部单曲id;
            musicListData[1]  = json.getReason().split(",");//歌单全部单曲id;
        }

        return  musicListData;
    }

    public static String[] GetMusicFM(String Cookie){
        String path ="https://api.feseliud.com/music163/musicFM.php";
        String JsonData = HttpAPIPOST(path,"Cookie=" + Cookie);
        //解析json字符串
        Gson gson = new Gson();
        MusicFM json = gson.fromJson(JsonData, MusicFM.class);
        //将数据存入数组
        String[] musicListData= {};
        if (json.getCode().equals("0")){
            musicListData  = json.getPlaylist().split(",");//歌单全部单曲id;
        }

        return  musicListData;
    }

    public static String download(String Path, String musicID, String downloadDir, Cache cache) {
        File file = null;
        String path=null;
        try {
            // 文件名
            String fileFullName = musicID + ".mp3";
            path = downloadDir + "\\" + fileFullName;
            file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            } else if (file.exists()){
                return path;
            }
            // 统一资源
            URL url = new URL(Path + musicID + ".mp3");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设定请求的方法
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接
            httpURLConnection.connect();

            // 文件大小
            int fileLength = httpURLConnection.getContentLength();
            URLConnection con = url.openConnection();

            BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());

            OutputStream out = new FileOutputStream(file);
            int size = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            while ((size = bin.read(buf)) != -1) {
                len += size;
                out.write(buf, 0, size);
            }

            bin.close();
            out.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cache.AddCache(new File(path).length());
        return path;
    }

    public static String like(String musicID,String cookie) {
        if (cookie != null && musicID != null){
            Gson gson = new Gson();
            return gson.fromJson(HttpAPIPOST("https://api.feseliud.com/music163/musiclike.php","id="+musicID+"&cookie="+cookie), like.class).getCode();
        }
        return null;
    }

    public static String[][] SimilarMusic(String musicID) {
        String[][] SimilarMusicData = {{},{},{}};
        Gson gson = new Gson();
        SimilarMusic json = gson.fromJson(HttpAPIPOST("https://api.feseliud.com/music163/similarmusic.php","id="+musicID), SimilarMusic.class);
        if(Objects.equals(json.getCode(), "0")){
            SimilarMusicData[0] =json.getPlaylist().split(",");
            SimilarMusicData[1] =json.getTitlet().split(",");
            SimilarMusicData[2] =json.getSinger().split(",");
        }
        return SimilarMusicData;
    }

    public static String[] AddMusic(String musicID,String musicList ,String cookie) {
        String[] AddMusicData = {"",""};
        Gson gson = new Gson();
        AddMusic json = gson.fromJson(HttpAPIPOST("https://api.feseliud.com/music163/addmusic.php","id="+musicID+"&pid="+musicList+"&cookie="+cookie), AddMusic.class);
        AddMusicData[0] = json.getCode();
        AddMusicData[1] = json.getCount();
        return AddMusicData;
    }

    public static String[] FmTrash(String musicID,String cookie) {
        String[] FmTrashData = {"",""};
        Gson gson = new Gson();
        AddMusic json = gson.fromJson(HttpAPIPOST("https://api.feseliud.com/music163/fm_trash.php","id="+musicID+"&cookie="+cookie), AddMusic.class);
        FmTrashData[0] = json.getCode();
        FmTrashData[1] = json.getCount();
        return FmTrashData;
    }


}
