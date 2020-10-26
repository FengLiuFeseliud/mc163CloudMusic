package fabricmod.mc163.CloudMusic.json;

public class mc163CloudMusic {
    public String downloadPath;
    public Cookie Cookie;
    public MusicList MusicList;
    public int volume;

    public class Cookie {
        public boolean CookieRead;
        public String CookieData;
    }

    public class MusicList {
        public String title;
        public String id;
    }
}
