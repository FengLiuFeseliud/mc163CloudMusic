<?php 
include "music163.php"; //方法配置文件
$cookie =@trim($_POST['cookie']);
$musicID =@trim($_POST['id']);

if(!($cookie == "" && $musicID == "")){
	fm_trash($cookie,$musicID);
}