<?php 
include "music163.php"; //方法配置文件
$cookie =@trim($_POST['cookie']);
$musicID =@trim($_POST['id']);
$pid =@trim($_POST['pid']);

if(!($cookie == "" && $musicID == "" && $pid == "")){
	addmusic($cookie,$musicID,$pid);
}