<?php 
include "music163.php"; //方法配置文件
$cookie =@trim($_POST['cookie']);
$musicID =@trim($POST['id']);
$pid =@trim($POST['pid']);

if(!($cookie == "" && $musicID == "" && $pid == "")){
	addmusic($cookie,$musicID,$pid);
}