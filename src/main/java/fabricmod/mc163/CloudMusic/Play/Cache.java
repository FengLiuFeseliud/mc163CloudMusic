package fabricmod.mc163.CloudMusic.Play;

import java.io.File;

public class Cache {
    public static String[] CacheLoad(String downloadPath){
        File file = new File(downloadPath);
        return file.list();
    }
}
