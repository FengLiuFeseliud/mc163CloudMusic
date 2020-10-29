package fabricmod.mc163.CloudMusic;

import com.google.gson.stream.MalformedJsonException;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabricmod.mc163.CloudMusic.Play.Cache;
import fabricmod.mc163.CloudMusic.Play.musicPlayThread;
import fabricmod.mc163.CloudMusic.json.mc163CloudMusic;
import fabricmod.mc163.CloudMusic.json.mc163CloudMusicjson;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import java.io.*;
import java.util.Objects;

public class CloudMusic implements ModInitializer {
	private String musicListID; //m163 get musiclist获取的歌单id
	private String[] musicListData; //m163 get musiclist获取的歌单信息
	private String[] musicIDList; //m163 get musiclist获取的歌单的全部单曲id
	private String[] musicReasonList; //m163 get dailymusiclist获取的日推的全部单曲推荐理由
	private String Path = "http://music.163.com/song/media/outer/url?id=";
	private String CookieData = null;
	private int volume = 1000; //音量 默认-33分
	private String type; // 播放模式
	private String[][] addMusicList ={{},{}};
	private musicPlayThread musicPlayThread = null;
	private Thread Thread;
	private Cache cache; //文件缓存相关

	//MalformedJsonException必须捕捉一发生会导致游戏崩溃
	public String[] Openmc163CloudMusicjson() throws IOException, MalformedJsonException {
		mc163CloudMusic mc163CloudMusic = mc163CloudMusicjson.get();
		this.volume = mc163CloudMusic.volume;
		this.CookieData = mc163CloudMusic.Cookie.CookieData;
		this.addMusicList[0] = mc163CloudMusic.MusicList.title.split(",");
		this.addMusicList[1] = mc163CloudMusic.MusicList.id.split(",");
		String[] Data = {"",""};
		Data[0] = mc163CloudMusic.Cache.downloadPath;
		Data[1] = mc163CloudMusic.Cache.CacheSize;
		return Data;
	}
	//检查线程状态 存活则停止播放
	public void ThreadStatus(){
		if(musicPlayThread != null){
			Thread.State state = Thread.getState();
			if(state != java.lang.Thread.State.TERMINATED){
				musicPlayThread.stop();
			}
		}
	}

	@Override
	public void onInitialize() {
		try {
			//检查配置文件是否存在 不存在则新建
			mc163CloudMusicjson.Set();
			//读取配置
			String[] Data = Openmc163CloudMusicjson();
			if(Data[0] != null){
				System.out.println("music163:缓存路径已加载!->" + Data[0]);
				//读取缓存占用
				cache = new Cache(Data[0],Integer.parseInt(Data[1]));
				cache.LookCache(null);
			}else {
				System.out.println("music163:缓存路径为null 请检查mc163CloudMusic.json -> downloadPath");
			}
		} catch (RuntimeException | MalformedJsonException e){
			System.out.println("music163:mc163CloudMusic.json加载失败 请检查json格式是否正确,如果无法修复可以删除后重启mc生成,再重新配置");
		} catch (IOException e){
			System.out.println("music163:mc163CloudMusic.json无法打开,如果无法修复可以删除后重启mc生成,再重新配置");
		} catch (ERROR.CacheSizeException e){
			System.out.println("CacheSize无法被正常配置 不能大于20 小于0,如果无法修复可以删除后重启mc生成,再重新配置");
		}

		CommandRegistrationCallback.EVENT.register((dispatcher2, dedicated2) -> {
			//父节点M163
			LiteralArgumentBuilder<ServerCommandSource> M163 = CommandManager.literal("m163");
			LiteralArgumentBuilder<ServerCommandSource> Get = CommandManager.literal("get");
			LiteralArgumentBuilder<ServerCommandSource> Set = CommandManager.literal("set");
			LiteralArgumentBuilder<ServerCommandSource> Play = CommandManager.literal("play");
			LiteralArgumentBuilder<ServerCommandSource> Add = CommandManager.literal("add");
			LiteralArgumentBuilder<ServerCommandSource> Cache = CommandManager.literal("cache");
			LiteralArgumentBuilder<ServerCommandSource> Help = CommandManager.literal("help");

			//Get的子节点musiclist
			M163.then(Get.then(CommandManager.literal("musiclist")
					.then(CommandManager.argument("id",StringArgumentType.string())
							.executes( musicCommand -> {
								String[][] ListData;
								ServerCommandSource source = musicCommand.getSource();
								//获取输入的id
								musicListID = StringArgumentType.getString(musicCommand, "id");
								//连接API
								source.sendFeedback(new LiteralText("music163:获取id->" + musicListID + "歌单中"),true);
								ListData = Http.GetMusicList(musicListID);

								Text.printMusicList(source, musicListID, ListData[0]);
								return 1;
							})
					)
			));
			//Get的子节点music
			M163.then(Get.then(CommandManager.literal("music")
					.then(CommandManager.argument("musicRow",IntegerArgumentType.integer())
							.executes( musicCommand -> {
								String[] musicData = null;
								ServerCommandSource source = musicCommand.getSource();
								//获取输入的id
								int musicRow = IntegerArgumentType.getInteger(musicCommand, "musicRow");
								//连接API
								source.sendFeedback(new LiteralText("music163:获取id->" + musicListID + "歌曲的第" + musicRow + "首单曲中"), true);
								try {
									musicData = Http.GetMusic(type,musicRow,musicIDList);
								} catch (Http.MusicRowException e) {
									ERROR.E402(source);
								}

								Text.printMusicData(source, musicRow, musicData, musicListID);
								return 1;
							})
					)
			));

			//Get的子节点SimilarMusic
			M163.then(Get.then(CommandManager.literal("SimilarMusic")
					.executes( musicCommand -> {
						this.musicPlayThread.SimilarMusic();

						return 1;
					})
			));

			//Set的子节点musiclist
			M163.then(Set.then(CommandManager.literal("musiclist")
					.then(CommandManager.argument("id",StringArgumentType.string())
							.executes( musicCommand -> {
								ServerCommandSource source = musicCommand.getSource();
								String[][] musicList;
								//获取输入的id
								musicListID = StringArgumentType.getString(musicCommand, "id");
								//连接API
								source.sendFeedback(new LiteralText("music163:获取id->" + musicListID + "歌单中"), true);
								musicList = Http.GetMusicList(musicListID);
								//从返回的数组中取出数据
								musicIDList = musicList[1];
								musicListData = musicList[0];

								Text.printMusicList(source, musicListID, musicListData);
								return 1;
							})
					)
			));

			//Set的子节点dailymusiclist
			M163.then(Set.then(CommandManager.literal("dailymusiclist")
					.executes( musicCommand -> {
						String[][] musicList;
						ServerCommandSource source = musicCommand.getSource();
						this.type = "dailymusicListPlay"; //设置为日推播放
						//连接API
						musicList = Http.GetDailyMusicList(Http.getCookie(this.CookieData,source));
						source.sendFeedback(new LiteralText("music163:日推获取成功!日推每天早上6点更新哦(〃'▽'〃)"), true);
						//从返回的数组中取出数据
						musicIDList = musicList[0];
						musicReasonList = musicList[1];

						return 1;
					})
			));

			//Set的子节点SimilarMusic
			M163.then(Set.then(CommandManager.literal("SimilarMusic")
					.executes( musicCommand -> {
						this.type ="SimilarMusic";
						String[][] SimilarMusic = this.musicPlayThread.SimilarMusic();
						//从返回的数组中取出数据
						musicIDList = SimilarMusic[0];

						return 1;
					})
			));

			//Play的子节点music
			M163.then(Play.then(CommandManager.literal("music")
					.then(CommandManager.argument("musicRow",IntegerArgumentType.integer())
							.executes( musicCommand ->{
								ThreadStatus();
								ServerCommandSource source = musicCommand.getSource();
								boolean SinglePlay = true;
								//获取输入的id
								int musicRow = IntegerArgumentType.getInteger(musicCommand, "musicRow");
								this.musicPlayThread = new musicPlayThread(SinglePlay,type,volume,musicRow,Path,cache,musicIDList,musicListID,musicReasonList,source,CookieData);
								this.Thread = new Thread(this.musicPlayThread);
								Thread.start();

								return 1;
							})
					)
			));

			//Play的子节点musiclist
			M163.then(Play.then(CommandManager.literal("musiclist")
					.executes( musicCommand ->{
						ThreadStatus();
						boolean SinglePlay = false;
						if (!(Objects.equals(this.type, "dailymusicListPlay") || Objects.equals(this.type, "SimilarMusic"))){
							this.type = "musicListPlay";
						}
						ServerCommandSource source = musicCommand.getSource();
						source.sendFeedback(new LiteralText("music163:开始播放歌单"), true);
						this.musicPlayThread = new musicPlayThread(SinglePlay,type,volume,1,Path,cache,musicIDList,musicListID,musicReasonList,source,CookieData);
						this.Thread = new Thread(this.musicPlayThread);
						Thread.start();

						return 1;
					})
			));

			//Play的子节点personalFM
			M163.then(Play.then(CommandManager.literal("personalFM")
					.executes( musicCommand ->{
						ThreadStatus();
						boolean SinglePlay = false;
						this.type = "musicFMPlay";
						ServerCommandSource source = musicCommand.getSource();
						source.sendFeedback(new LiteralText("music163:已进入私人FM模式~"), true);

						this.musicPlayThread = new musicPlayThread(SinglePlay,type,volume,1,Path,cache,musicIDList,musicListID,musicReasonList,source,CookieData);
						this.Thread = new Thread(this.musicPlayThread);
						Thread.start();

						return 1;
					})
			));

			//Play的子节点stop
			M163.then(Play.then(CommandManager.literal("stop")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						source.sendFeedback(new LiteralText("music163:停止播放"), true);

						this.musicPlayThread.stop();

						return 1;
					})
			));

			//Play的子节点next
			M163.then(Play.then(CommandManager.literal("next")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						source.sendFeedback(new LiteralText("music163:下一首"), true);

						this.musicPlayThread.NextSong();

						return 1;
					})
			));

			//Play的子节点volumeUp
			M163.then(Play.then(CommandManager.literal("volumeUp")
						.then(CommandManager.argument("volume",IntegerArgumentType.integer(0, 85))
							.executes( musicCommand ->{
								ServerCommandSource source = musicCommand.getSource();
								//获取输入的volume
								int Volume = IntegerArgumentType.getInteger(musicCommand, "volume");
								this.volume = this.musicPlayThread.volumeUp(Volume);

								return 1;
							})
					)
			));

			//Play的子节点volumeDown
			M163.then(Play.then(CommandManager.literal("volumeDown")
					.then(CommandManager.argument("volume",IntegerArgumentType.integer(0, 85))
							.executes( musicCommand ->{
								ServerCommandSource source = musicCommand.getSource();
								//获取输入的volume
								int Volume = IntegerArgumentType.getInteger(musicCommand, "volume");
								this.volume = this.musicPlayThread.volumeDown(Volume);

								return 1;
							})
					)
			));

			//Add的子节点like
			M163.then(Add.then(CommandManager.literal("like")
					.executes( musicCommand ->{
						this.musicPlayThread.like();

						return 1;
					})
			));

			M163.then(Add.then(CommandManager.argument("addMusicListRow",IntegerArgumentType.integer())
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						int addMusicListRow = IntegerArgumentType.getInteger(musicCommand, "addMusicListRow");
						if (addMusicListRow-1 > -1 && addMusicListRow < addMusicList.length){
							this.musicPlayThread.AddMusic(this.addMusicList[1][addMusicListRow-1],this.addMusicList[0][addMusicListRow-1]);
						}else {
							ERROR.E406(source);
						}

						return 1;
					})
			));

			//Add的子节点musiclist
			M163.then(Add.then(CommandManager.literal("musiclist")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						Text.printAddMusicList(this.addMusicList,source);

						return 1;
					})
			));

			//Add的子节点trash
			M163.then(Add.then(CommandManager.literal("trash")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						this.musicPlayThread.FmTrash();
						this.musicPlayThread.NextSong();

						return 1;
					})
			));

			//Cache的子节点lookcache
			M163.then(Cache.then(CommandManager.literal("lookcache")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						cache.LookCache(source);

						return 1;
					})
			));

			//Cache的子节点looksets
			M163.then(Cache.then(CommandManager.literal("looksets")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						cache.Looksets(source);

						return 1;
					})
			));

			//Cache的子节点cachedelete
			M163.then(Cache.then(CommandManager.literal("cachedelete")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						cache.CacheDelete(source,false);

						return 1;
					})
			));

			//Help的子节点get
			M163.then(Help.then(CommandManager.literal("get")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						Text.HelpGet(source);
						return 1;
					})
			));

			//Help的子节点set
			M163.then(Help.then(CommandManager.literal("set")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						Text.HelpSet(source);
						return 1;
					})
			));

			//Help的子节点play
			M163.then(Help.then(CommandManager.literal("play")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						Text.HelpPlay(source);
						return 1;
					})
			));

			//Help的子节点play
			M163.then(Help.then(CommandManager.literal("add")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						Text.HelpAdd(source);
						return 1;
					})
			));

			//Help的子节点json
			M163.then(Help.then(CommandManager.literal("json")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						Text.HelpJson(source);
						return 1;
					})
			));

			//Help的子节点cache
			M163.then(Help.then(CommandManager.literal("cache")
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						Text.HelpCache(source);
						return 1;
					})
			));

			M163.then(Help
					.executes( musicCommand ->{
						ServerCommandSource source = musicCommand.getSource();
						Text.Help(source);
						return 1;
					})
			);

			M163.then(CommandManager.argument("type",StringArgumentType.string())
					.executes(musicCommand -> {
						ServerCommandSource source = musicCommand.getSource();
						//获取输入的type
						String type = StringArgumentType.getString(musicCommand, "type");
						try {
							if (Objects.equals(type, "-m")) {
								//打印Set设置的歌单信息
								Text.printMusicList(source, musicListID, musicListData);
								return 1;
							}else if (Objects.equals(type, "-c")){
								//重载配置
								String[] Data = Openmc163CloudMusicjson();
								cache = new Cache(Data[0],Integer.parseInt(Data[1]));
								cache.Looksets(source);
								source.sendFeedback(new LiteralText("music163:配置文件已重载!"), true);
								return 1;
							} else if (Objects.equals(type, "-v")){
								if(this.volume == 1000){
									throw new ERROR.VolumeException();
								}
								source.sendFeedback(new LiteralText("music163:当前音量为" + this.volume + "分贝"), true);
								return 1;
							}
							throw new ERROR.NotCommandException();
						} catch (ERROR.NotCommandException e) {
							ERROR.E404(source);
						} catch (NullPointerException e){
							ERROR.E401(source);
						} catch(RuntimeException | MalformedJsonException e){
							ERROR.E101(source);
						} catch (IOException e){
							ERROR.E102(source);
						} catch (ERROR.VolumeException e){
							ERROR.E103(source);
						} catch (ERROR.CacheSizeException e){
							ERROR.E407(source);
						}
						return 1;
					}));

			dispatcher2.register(M163);
		});

	}
}
