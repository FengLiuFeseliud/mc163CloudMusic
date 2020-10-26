<?php 
include "music163.php"; //方法配置文件

$id =@trim($_POST['id']);

if($id != ""){
	//获取歌单信息和歌单所有歌曲id 并输出json 字符串
	echo json_encode(music_data($id));
}
