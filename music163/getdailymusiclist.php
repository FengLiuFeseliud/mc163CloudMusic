<?php 
include "music163.php"; //方法配置文件

$cookie =@trim($_POST['Cookie']);

if($cookie != ""){
	//获取日堆 并输出json 字符串
	echo json_encode(music_login_status($cookie));
}