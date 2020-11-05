package fabricmod.mc163.CloudMusic;

import fabricmod.mc163.CloudMusic.json.RecommendMusic;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Text{

    //分割Data 并一行行打印
    public static void printData(String Data,ServerCommandSource source){
        String[] DataList ={};
        DataList =Data.split(",");
        for (int i = 0; i< DataList.length; i++){
            source.sendFeedback(new LiteralText(DataList[i]), true);
        }
    }

    //分页打印集合
    public static void printListData(List<RecommendMusic.Musiclist> List, ServerCommandSource source, int page,int pageRow){
        if (pageRow*page > List.size()){
            source.sendFeedback(new LiteralText("music163:最大页数"+(int)Math.ceil(List.size() / pageRow)), true);
            return;
        } else if ( 1 > page){
            source.sendFeedback(new LiteralText("music163:页数不能小于1!"), true);
            return;
        }
        source.sendFeedback(new LiteralText("music163:当前页数"+page+" 最大页数"+(int)Math.ceil(List.size() / pageRow)), true);
        java.util.List ID_list = new ArrayList();
        for (int i = -pageRow+(pageRow*page); i < pageRow*page; i++){
            RecommendMusic.Musiclist Musiclist = List.get(i);
            source.sendFeedback(new LiteralText(i+1+". "+Musiclist.getName()+" "+Musiclist.getCopywriter()), true);
            ID_list.add(Musiclist.getId());
        }
    }

    public static void printSimilarMusic(String[][] Data,String musicTit,ServerCommandSource source){
        source.sendFeedback(new LiteralText("music163:" + musicTit + "的相似歌曲"), true);
        source.sendFeedback(new LiteralText("music163:使用[/m163 set SimilarMusic]后以序号播放或列表播放"), true);
        source.sendFeedback(new LiteralText("-----------------------------------"), true);
        for (int i = 0; i< Data[0].length; i++){
            source.sendFeedback(new LiteralText((i+1)+": "+Data[1][i]+" - "+Data[2][i]), true);
        }
        source.sendFeedback(new LiteralText("-----------------------------------"), true);
    }

    public static void printAddMusicList(String[][] AddMusicList,ServerCommandSource source){
        source.sendFeedback(new LiteralText("music163:已设置的歌单"), true);
        source.sendFeedback(new LiteralText("music163:使用[/m163 add [序号] ]选择歌单收藏当前单曲"), true);
        source.sendFeedback(new LiteralText("-----------------------------------"), true);
        for (int i = 0; i< AddMusicList[1].length; i++){
            source.sendFeedback(new LiteralText((i+1)+": "+AddMusicList[0][i]+" - "+AddMusicList[1][i]), true);
        }
        source.sendFeedback(new LiteralText("-----------------------------------"), true);
    }


    public static void printMusicList(ServerCommandSource source,String musicListID,String[] musicListData){
        //将时间戳转换为时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
        long lt = Long.parseLong(musicListData[2]);
        Date date = new Date(lt);
        String time = simpleDateFormat.format(date);
        String Data = "music163:获取id->" + musicListID + "歌单成功!," +
                "歌单:" + musicListData[0] + "," +
                "作者:" + musicListData[1] + "," +
                "创建日期:" + time + "," +
                "单曲数:" + musicListData[3] + "," +
                "播放数:" + musicListData[4];
        printData(Data,source);
    }
    public static void printMusicData(ServerCommandSource source,int musicRow,String[] musicData,String musicListID){
        String Data ="music163:获取id->" + musicListID + "歌单的第" + musicRow + "首单曲成功!," +
                "-----------------------------------," +
                musicData[0]+ " " + musicData[1] + "," +
                "歌手:" + musicData[2] + "," +
                "专辑:" + musicData[3] + "," +
                "单曲id:" + musicData[5] + "," +
                "-----------------------------------";
        printData(Data,source);
    }
    public static void printDailyMusicData(ServerCommandSource source,int musicRow,String[] musicData,String[] musicReasonList){
        String Data ="music163:获取日推的第" + musicRow + "首单曲成功!," +
                "-----------------------------------," +
                musicData[0]+ " " + musicData[1] + "," +
                "歌手:" + musicData[2] + "," +
                "专辑:" + musicData[3] + "," +
                "单曲id:" + musicData[5] + "," +
                musicReasonList[musicRow - 1] + "," +
                "-----------------------------------";
        printData(Data,source);
    }

    public static void printMusicFMData(String type,int musicRow,ServerCommandSource source,String[] musicData){
        String Data = null;
        if (Objects.equals(type, "musicFMPlay")){
            Data ="music163:获取私人FM的下一首单曲成功!,";
        } else if (Objects.equals(type, "SimilarMusic")){
            Data ="music163:获取第"+musicRow+"首相似歌曲成功!,";
        } else {
            Data ="music163:获取第"+musicRow+"首推荐新单曲成功!,";
        }
        Data = Data +
                "-----------------------------------," +
                musicData[0]+ " " + musicData[1] + "," +
                "歌手:" + musicData[2] + "," +
                "专辑:" + musicData[3] + "," +
                "单曲id:" + musicData[5] + "," +
                "-----------------------------------";
        printData(Data,source);
    }

    //帮助信息
    public static void HelpGet(ServerCommandSource source){
        String Data ="music163:Get节点的帮助信息," +
                "-----------------------------------," +
                "Get节点的所有指令获取的信息只打印不保存," +
                "[/m163 get music [行数] ] 获取已设置歌单指定行的歌曲信息," +
                "[/m163 get musiclist [id] ] 获取指定id歌单信息," +
                "[/m163 get SimilarMusic] 获取当前播放歌曲的相似歌曲," +
                "[/m163 get recommend [布尔]] 获取推荐歌单/新歌曲," +
                "      布尔为true获取推荐歌单 false为获取新歌曲," +
                "[/m163 get page [页数]] 翻页," +
                "需保存获取的信息请使用Set节点的指令," +
                "-----------------------------------";
        printData(Data,source);
    }

    public static void HelpSet(ServerCommandSource source){
        String Data ="music163:Set节点的帮助信息," +
                "-----------------------------------," +
                "[/m163 set [序号] ] 设置当前歌单为指定序号推荐歌单," +
                "[/m163 set dailymusiclist] 设置当前歌单为日推," +
                "[/m163 set musiclist [id] ] 设置当前歌单为指定id歌单," +
                "[/m163 set SimilarMusic] 设置当前歌单为当前播放歌曲的相似歌曲," +
                "使用Play节点的指令以播放," +
                "使用[/m163 help json] 获取如何设置Cookie," +
                "-----------------------------------";
        printData(Data,source);
    }

    public static void HelpPlay(ServerCommandSource source){
        String Data ="music163:Play节点的帮助信息," +
                "-----------------------------------," +
                "[/m163 play musicl [行数] ] 播放当前歌单指定行的歌曲," +
                "[/m163 play musiclist] 播放当前歌单," +
                "[/m163 play personalFM] 播放私人FM," +
                "[/m163 play stop] 停止播放," +
                "[/m163 play next] 下一首," +
                "[/m163 play volumeUp [音量] ] 提升播放音量 一次最高提升6," +
                "[/m163 play volumeDown [音量] ] 降低播放音量 一次最高降低6," +
                "使用[/m163 help json] 获取如何设置Cookie," +
                "-----------------------------------";
        printData(Data,source);
    }

    public static void HelpAdd(ServerCommandSource source){
        String Data ="music163:Add节点的帮助信息," +
                "-----------------------------------," +
                "[/m163 add like] 将当前播放歌曲添加进喜欢," +
                "[/m163 add musiclist] 获取可以添加歌曲的歌单," +
                "[/m163 add trash ] 将当前播放歌曲从私人FM中移除至垃圾桶," +
                "[/m163 add [序号] ] 将当前播放歌曲添加进指定序号歌单," +
                "使用[/m163 help json] 获取如何添加歌单," +
                "-----------------------------------";
        printData(Data,source);
    }

    public static void HelpJson(ServerCommandSource source){
        String Data ="music163:如何配置mc163CloudMusic.json?," +
                "-----------------------------------," +
                "配置前须知-> 所有数据必须用英文双引号包裹 除了有特别说明的," +
                "配置前须知-> 旧版本请删除配置文件重新生成!!!," +
                "配置前须知-> 玩坏了可以删除了重启游戏生成," +
                "配置前须知-> 可以开着游戏配置 配置完了使用[/m163 -c]重载," +
                "downloadPath为当前缓存路径 将指定的缓存路径替换当前缓存路径就行," +
                "CacheSize为缓存大小 最大20GB 最小1GB 配置时不能加上小数和GB!!!," +
                "volume为默认音量每次重启都会设置为该值 该值为int不能使用双引号包裹," +
                "pageRow为推荐歌单/新音乐 一页的打印行数 最好不大于10因为推荐新音乐就10首,"+
                "CookieData为你网易云音乐用户Cookie," +
                "如何知道我的Cookie? 登录网页端(wed)的网易云音乐," +
                "然后在网页端(wed)的网易云音乐 打开开发者工具(检查元素)选择Console(控制台)," +
                "在最低下的> 输入document.cookie回车复制即可," +
                "如何设置我收藏歌的歌单? 首先找到MusicList 下的title id," +
                "title为歌单标题 id为歌单id 多个用英文逗号分割 如," +
                "\"title\": \"FM，Touhou EDM\"," +
                "\"id\": \"2868768606，2500010465\"," +
                "这里演示使用的是中文逗号实际操作请使用英文逗号!!!,"+
                "-----------------------------------";
        printData(Data,source);
    }

    public static void Help(ServerCommandSource source){
        String Data ="music163:其他指令," +
                "-----------------------------------," +
                "[/m163 -m] 打印Set设置的歌单信息," +
                "[/m163 -v] 查看当前音量," +
                "[/m163 -c] 重载mc163CloudMusic.json," +
                "使用[/m163 help [节点] ] 获取其他帮助信息 如," +
                "[/m163 help set],"+
                "-----------------------------------";
        printData(Data,source);
    }

    public static void HelpCache(ServerCommandSource source) {
        String Data ="music163:Cache节点的帮助信息," +
                "-----------------------------------," +
                "[/m163 cache lookcache] 查看缓存占用," +
                "[/m163 cache looksets] 查看缓存设置," +
                "[/m163 cache cachedelete] 清空缓存文件," +
                "-----------------------------------";
        printData(Data,source);
    }
}
