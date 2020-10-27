package fabricmod.mc163.CloudMusic.Play;

import fabricmod.mc163.CloudMusic.Http;
import net.minecraft.server.command.ServerCommandSource;
import java.util.Objects;

public class musicPlayThread implements Runnable{
    private boolean SinglePlay;
    private boolean onplay = true;
    private int ListRow;
    private String type;
    private String Cookie;
    private music music;
    private int musicRow;

    public musicPlayThread(boolean SinglePlay,String type,int volume,int musicRow,String path,String downloadPath,String[] musicIDList,String musicListID,String[] musicReasonList,ServerCommandSource source,String Cookie){
        this.type = type;
        this.Cookie = Cookie;
        this.musicRow = musicRow;
        this.SinglePlay = SinglePlay;
        music = new music(type,volume, musicRow,path,downloadPath,musicReasonList,musicIDList,musicListID,source);
        if(!Objects.equals(type, "musicFMPlay" ) && !SinglePlay){
            this.ListRow = musicIDList.length;
        } else if (Objects.equals(type, "musicFMPlay")){
            this.ListRow = 3;
            music.SetFMData(Http.GetMusicFM(Cookie));
        }
    }

    @Override
    public void run(){
        while (onplay){
            music.Play(musicRow);
            if (!SinglePlay && musicRow <= ListRow){
                musicRow++;
                if(Objects.equals(this.type, "musicFMPlay" ) && musicRow < 4){
                    musicRow = 1;
                    music.SetFMData(Http.GetMusicFM(Cookie));
                }
            } else {
                return;
            }
        }
    }

    public void stop(){
        this.onplay = false;
        this.music.stop();
    }

    public int volumeUp(int volume){
        return this.music.volumeUp(volume);
    }
    public int volumeDown(int volume){
        return this.music.volumeDown(volume);
    }
    public void like(){ this.music.like(this.Cookie); }
    public String[][] SimilarMusic(){
        return this.music.SimilarMusic();
    }
    public void AddMusic(String musicListID,String musicListTit){ this.music.AddMusic(musicListID,musicListTit,this.Cookie); }
}
