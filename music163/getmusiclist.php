<?php 
include "music163.php"; //方法配置文件

$id =@trim($_POST['id']);
$cookie =@trim($_POST['cookie']);

if($id != ""){
	//获取歌曲信息 并输出json 字符串
	echo json_encode(music_idlist($id,$cookie));
}



