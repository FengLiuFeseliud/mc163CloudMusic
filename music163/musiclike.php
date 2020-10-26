<?php 
include "music163.php"; //方法配置文件

$cookie =@trim($_POST['cookie']);
$musicID =@trim($_POST['id']);
if($musicID != "" && $cookie != ""){
	musiclike($cookie,$musicID);
}