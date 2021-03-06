package fabricmod.mc163.CloudMusic.Play;

import fabricmod.mc163.CloudMusic.ERROR;
import fabricmod.mc163.CloudMusic.Http;
import fabricmod.mc163.CloudMusic.Text;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class music {
    private Cache cache;
    private int volume;
    private String type;
    private String path;
    private String musicListID;
    private String[] musicIDList;
    private String[] musicReasonList;
    private ServerCommandSource source;
    private SourceDataLine play;
    private String musicID = null;
    private String musictit = null;
    private boolean load =true;
    private String[] musicData;
    private int musicRow;

    public music(String type,int volume,int musicRow,String path,Cache cache,String[] musicReasonList,String[] musicIDList,String musicListID,ServerCommandSource source){
        this.volume = volume;
        this.path = path;
        this.cache = cache;
        this.source = source;
        this.type = type;
        this.musicReasonList = musicReasonList;
        this.musicListID = musicListID;
        if (Objects.equals(type, "CachePlay")){
            //加载缓存文件
            source.sendFeedback(new LiteralText("music163:加载缓存文件 加载时长取决于文件量"), true);
            this.musicIDList = cache.CacheLoad(cache.getPath());
        } else {
            this.musicIDList = musicIDList;
        }
    }

    public void Play(int musicRow){
        this.musicRow = musicRow;
        String CachePath;
        try {
            CachePath = GetPlayData(musicRow);
        } catch (ERROR.VolumeException e) {
            ERROR.E103(source);
            return;
        } catch (Http.MusicRowException e){
            ERROR.E402(source);
            return;
        }
        try {
            load =true;
            // 文件流
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(CachePath));
            // 文件编码
            AudioFormat audioFormat = audioInputStream.getFormat();
            // 转换文件编码
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                System.out.println(audioFormat.getEncoding());
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false);
                audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
            }

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
            play = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            play.open(audioFormat);
            //设置音量
            int SetVolume = volume;
            FloatControl gainControl = (FloatControl) this.play.getControl(FloatControl.Type.MASTER_GAIN);
            while (Math.abs(SetVolume) > 6){
                if(SetVolume < 0){
                    gainControl.setValue(gainControl.getValue() - 6);
                    SetVolume = SetVolume + 6;
                    if (SetVolume > -6){
                        gainControl.setValue(gainControl.getValue() + SetVolume);
                        SetVolume = 0;
                    }
                }else {
                    gainControl.setValue(gainControl.getValue() + 6);
                    SetVolume = SetVolume - 6;
                    if (SetVolume < 6){ ;
                        gainControl.setValue(gainControl.getValue() + SetVolume);
                        SetVolume = 0;
                    }
                }
            }
            if(SetVolume == 0){
                source.sendFeedback(new LiteralText("music163:音量载入完成!当前音量" + gainControl.getValue()), true);
            }
            play.start();

            int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
            // 将流数据逐渐写入数据行,边写边播
            int numBytes = 1024 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            source.sendFeedback(new LiteralText("music163:流数据写入数据行"), true);
            while (audioInputStream.read(audioBytes) != -1 && load) {
                play.write(audioBytes, 0, audioBytes.length);
            }
            play.drain();
            play.stop();
            play.close();
            cache.LookCacheForDelete(source);

        } catch (UnsupportedAudioFileException e) {
            source.sendFeedback(new LiteralText("music163:该为vip歌曲自动跳过 请填写有vip的用户cookie以播放"), true);
            e.printStackTrace();
        } catch (IOException e){
            source.sendFeedback(new LiteralText("music163:未知错误数据写入失败"), true);
            e.printStackTrace();
        } catch (Exception e){
            source.sendFeedback(new LiteralText("music163:未知错误创建play对象失败"), true);
            e.printStackTrace();
        }
    }

    public String GetPlayData(int musicRow) throws ERROR.VolumeException,Http.MusicRowException {
        if(this.volume == 1000){
            throw new ERROR.VolumeException();
        }
        this.musicData = Http.GetMusic(this.type,musicRow,musicIDList);
        this.musicID = musicData[5];
        this.musictit = this.musicData[0]+" "+this.musicData[1];
        if (type == null || Objects.equals(type, "musicListPlay")){
            Text.printMusicData(source,musicRow,musicData,musicListID);
        } else if(Objects.equals(type, "musicFMPlay")){
            Text.printMusicFMData(type,musicRow,source,musicData);
        } else if(Objects.equals(type, "dailymusicListPlay")){
            Text.printDailyMusicData(source,musicRow,musicData,musicReasonList);
        } else if(Objects.equals(type, "SimilarMusic")){
            Text.printMusicFMData(type,musicRow,source,musicData);
        } else if(Objects.equals(type, "recommendMusic")){
            Text.printMusicFMData(type,musicRow,source,musicData);
        }
        return Http.download(path,musicID,cache.getPath(),cache);
    }

    public int volumeUp (int Volume){
        if(Volume > 6){
            try {
                throw new ERROR.VolumeValueException();
            } catch (ERROR.VolumeValueException e) {
                ERROR.E403(source);
            }
            return volume;
        }
        FloatControl gainControl = (FloatControl) this.play.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gainControl.getValue() + Volume);
        source.sendFeedback(new LiteralText("music163:音量增大" + Volume + "分贝 当前分贝" +  gainControl.getValue()), true);
        this.volume = volume + Volume;
        return this.volume;
    }

    public int volumeDown (int Volume){
        if( Volume > 6){
            try {
                throw new ERROR.VolumeValueException();
            } catch (ERROR.VolumeValueException e) {
                ERROR.E403(source);
            }
            return volume;
        }
        FloatControl gainControl = (FloatControl) this.play.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gainControl.getValue() - Volume);
        source.sendFeedback(new LiteralText("music163:音量减小" + Volume + "分贝 当前分贝" +  gainControl.getValue()), true);
        this.volume = volume - Volume;
        return this.volume;
    }

    public void SetFMData(String[] FMData) {
        this.musicIDList = FMData;
    }

    public void stop(){
        this.load = false;
        this.play.stop();
        this.play.close();
    }

    public void like(String Cookie){
        if(this.musicID != null && Cookie != null){
            String code = Http.like(this.musicID,Cookie);
            if (Objects.equals(code, "200")){
                source.sendFeedback(new LiteralText("music163:喜欢了单曲"+this.musictit), true);
            } else {
                ERROR.E405(source);
            }
        }
    }

    public String[][] SimilarMusic(){
        String[][] SimilarMusicData = Http.SimilarMusic(this.musicID);
        Text.printSimilarMusic(SimilarMusicData,this.musictit,source);
        return SimilarMusicData;
    }

    public void AddMusic(String musicListID,String musicListTit,String cookie){
        String[] AddMusicData = Http.AddMusic(musicID,musicListID,cookie);
        if (Objects.equals(AddMusicData[0], "200")){
            source.sendFeedback(new LiteralText("music163:向"+musicListTit+"收藏了单曲"+this.musictit), true);
            source.sendFeedback(new LiteralText("music163:"+musicListTit+"目前单曲数"+AddMusicData[1]), true);
        }else if(Objects.equals(AddMusicData[0], "502")){
            source.sendFeedback(new LiteralText("music163:歌单"+musicListTit+"已存在单曲"+musictit), true);
        }
    }

    public void FmTrash(String cookie){
        String[] FmTrash = Http.FmTrash(musicID,cookie);
        if (Objects.equals(FmTrash[0], "200")){
            source.sendFeedback(new LiteralText("music163:向垃圾桶添加了单曲"+this.musictit), true);
        }
    }

}
