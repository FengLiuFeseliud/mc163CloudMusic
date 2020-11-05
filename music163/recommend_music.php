<?php 
include "music163.php"; //方法配置文件
$cookie =@trim($_POST['cookie']);
$bool =@trim($_POST['bool']);
if($cookie != ''){
	echo json_encode(recommend_music($cookie,$bool));
}