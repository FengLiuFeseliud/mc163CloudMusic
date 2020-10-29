package fabricmod.mc163.CloudMusic.Play;

import fabricmod.mc163.CloudMusic.ERROR;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.io.File;

public class Cache {
    private long allB,Cache,MaxB = 0;
    private String downloadPath;
    private int CacheSize;
    private static final long Byte = 1000000000;

    public Cache(String downloadPath,int CacheSize) throws ERROR.CacheSizeException {
        File file = new File(downloadPath);
        if (!file.exists()){
            file.mkdir();
            System.out.println("music163:当前目录没有该文件夹已自动创建");
        }
        this.downloadPath = downloadPath;
        File[] FileList = file.listFiles();
        for (File value : FileList) {
            if (value.exists()) {
                long B = value.length();
                allB += B;
                Cache++;
            }
        }
        if (CacheSize > 0 && CacheSize <= 20){
            MaxB = Byte*CacheSize;
            this.CacheSize = CacheSize;
        }else {
            throw new ERROR.CacheSizeException();
        }
    }

    public static String getSize(long size){
        long rest = 0;
        if(size < 1024){
            return String.valueOf(size) + "B";
        }else{
            size /= 1024;
        }

        if(size < 1024){
            return String.valueOf(size) + "KB";
        }else{
            rest = size % 1024;
            size /= 1024;
        }

        if(size < 1024){
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((rest * 100 / 1024 % 100)) + "MB";
        }else{
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }

    public String[] CacheLoad(String downloadPath){
        File file = new File(downloadPath);
        return file.list();
    }

    public File[] CacheFile(String downloadPath){
        File file = new File(downloadPath);
        return file.listFiles();
    }

    public void CacheDelete(ServerCommandSource source,boolean type){
        File[] FileNList = new File(downloadPath).listFiles();
       for (File file : FileNList) {
           if (file.exists()){
               if (!file.delete()){
                   source.sendFeedback(new LiteralText("music163:"+file.getName()+"跳过删除"), true);
               }
           }
       }
       if (type){
           source.sendFeedback(new LiteralText("music163:本次自动删除"+Cache+"件缓存文件,节省了"+getSize(allB)), true);
       }else {
           source.sendFeedback(new LiteralText("music163:本次删除"+Cache+"件缓存文件,节省了"+getSize(allB)), true);
       }
        Cache = 0;
        allB = 0;
    }

    public void AddCache(long B){
        this.allB+=B;
        this.Cache++;
    }

    public void LookCache(ServerCommandSource source){
        if (source != null){
            source.sendFeedback(new LiteralText("music163:共有"+Cache+"件缓存文件,已占用"+getSize(allB)), true);
        }else {
            System.out.println("music163:共有"+Cache+"件缓存文件,已占用"+getSize(allB));
        }
    }

    public void Looksets(ServerCommandSource source) {
        source.sendFeedback(new LiteralText("music163:已设置缓存路径为->"+downloadPath), true);
        source.sendFeedback(new LiteralText("music163:最大缓存大小"+CacheSize+"GB "+MaxB+"字节"), true);
    }

    public String getPath(){ return this.downloadPath; }
    public void LookCacheForDelete(ServerCommandSource source){ if (allB > MaxB) CacheDelete(source,true); }

}
