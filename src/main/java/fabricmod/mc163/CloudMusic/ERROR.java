package fabricmod.mc163.CloudMusic;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class ERROR {

    //分割ErrMsg 并一行行打印
    public static void printError(String ErrMsg,ServerCommandSource source){
        String[] SplitError ={};
        SplitError =ErrMsg.split(",");
        for (int i = 0; i< SplitError.length; i++){
            source.sendFeedback(new LiteralText(SplitError[i]), true);
        }
    }

    public static void E101(ServerCommandSource source){
        String ErrMsg="music163:mc163CloudMusic.json加载失败," +
                "请检查json格式是否正确," +
                "如果无法修复可以删除后重启mc生成,再重新配置";
        printError(ErrMsg,source);
    }

    public static void E102(ServerCommandSource source){
        String ErrMsg="music163:mc163CloudMusic.json无法打开," +
                "如果无法修复可以删除后重启mc生成再重新配置";
        printError(ErrMsg,source);
    }

    public static void E103(ServerCommandSource source){
        String ErrMsg="music163:音量未被正常配置," +
                "请检查mc163CloudMusic.json," +
                "如果无法修复可以删除后重启mc生成，再重新配置";
        printError(ErrMsg,source);
    }

    public static void E401(ServerCommandSource source) {
        String ErrMsg="music163:还没有设置要播放的歌单," +
                "使用[/m163 set musiclist 歌单id]来设置歌单," +
                "使用[/m163 help set]获取set子节点的帮助信息";
        printError(ErrMsg,source);
    }

    public static void E402(ServerCommandSource source){
        String ErrMsg="music163:musicRow填写不正确," +
                "musicRow不能小于0或大于歌单单曲数," +
                "使用[/m163 help get]获取get子节点的帮助信息";
        printError(ErrMsg,source);
    }

    public static void E403(ServerCommandSource source){
        String ErrMsg="music163:volume填写不正确," +
                "一次只能最大增加或减少6分贝," +
                "使用[/m163 help play]获取play子节点的帮助信息";
        printError(ErrMsg,source);
    }

    public static void E404(ServerCommandSource source){
        String ErrMsg="music163:没有该指令," +
                "使用[/m163 help -]获取帮助信息";
        printError(ErrMsg,source);
    }

    public static void E405(ServerCommandSource source){
        String ErrMsg="music163:操作失败," +
                "请检查mc163CloudMusic.json的cookie," +
                "使用[/m163 -c]重载mc163CloudMusic.json";
        printError(ErrMsg,source);
    }

    public static void E406(ServerCommandSource source){
        String ErrMsg="music163:addMusicListRow填写不正确," +
                "使用[/m163 help add]获取add子节点的帮助信息";
        printError(ErrMsg,source);
    }

    //自定义错误
    public static class VolumeValueException extends Exception {
        public VolumeValueException() {
            super("一次只能最大增加或减少6分贝");
        }
    }

    public static class NotCommandException extends Exception {
        public NotCommandException() {
            super("没有该指令");
        }
    }

    public static class VolumeException extends Exception {
        public VolumeException() {
            super("音量未被正常配置");
        }
    }


}
