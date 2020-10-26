<?php 
include "music163.php"; //方法配置文件

$musicID =@trim($_POST['id']);
if($musicID != ""){
	echo json_encode(similarmusic($musicID));
}