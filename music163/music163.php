<?php 


function music_login($login,$id,$pass){
	
	if($login =="email"){
		$http = curl_init();
		curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/login?email=$id&password=$pass");
		curl_setopt($http, CURLOPT_RETURNTRANSFER, true);
		    curl_setopt($ch, CURLOPT_POST,1);
		
		$data =json_decode(curl_exec($http));
		print_r($data);
	}
	if($login =="phone"){
		$http = curl_init();
		curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/login/cellphone?phone=$id&password=$pass");
		curl_setopt($http, CURLOPT_RETURNTRANSFER, true);

		$data=json_decode(curl_exec($http));
		print_r($data);
	}
}

function music_login_status($cookie){
	$http = curl_init();
	curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/recommend/songs");
	curl_setopt($http, CURLOPT_HEADER, 0);
	curl_setopt($http, CURLOPT_POST, 1);
	$header = array('withCredentials: true');
	curl_setopt($http, CURLOPT_COOKIE, $cookie);
    curl_setopt($http, CURLOPT_HTTPHEADER, $header);
	curl_setopt($http, CURLOPT_RETURNTRANSFER, true);
	
	$data =json_decode(curl_exec($http),true);
	$music =$data['recommend'];
	//初始化id
	$music_id['playlist'] =$music[0]['id'];
	//初始化reason
	$music_id['reason'] =$music[0]['reason'];
	for($i =1;$i < count($music);$i++){
		//id
		$music_id['playlist'] =$music_id['playlist'].','.$music[$i]['id'];
		//根据你可能喜欢的单曲reason
		$music_id['reason'] =$music_id['reason'].','.$music[$i]['reason'];
	}
	$music_id['code'] ='0';
	return $music_id;
}

function music_idlist($MusicListID){
	//获取歌单所有歌曲id+歌曲url
	$http = curl_init();
	curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/playlist/detail?id=$MusicListID");
	curl_setopt($http, CURLOPT_HEADER, 0);
	curl_setopt($http, CURLOPT_RETURNTRANSFER, true);
	
	$data =json_decode(curl_exec($http),true);
	
	$music_id['code'] ='0';
	//标题
	$music_id['title'] =$data['playlist']['name'];
	//歌单作者
	$music_id['creator'] =$data['playlist']['creator']['nickname'];
	//歌单创建日期
	$music_id['creationdate'] =$data['playlist']['createTime'];
	//歌单播放数
	$music_id['playcount'] =$data['playlist']['playCount'];
	//歌单全部单曲的id
	$playlist =$data['playlist']['trackIds'];
	//初始化playlist
	$music_id['playlist'] =$playlist[0]['id'];
	
	for($i =1; $i < count($playlist) ;$i++){
		$music_id['playlist']=$music_id['playlist'].','.$playlist[$i]['id'];
	}
	return $music_id;
}

function music_data($MusicID){
	//获取单曲信息
	$http = curl_init();
	curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/song/detail?ids=$MusicID");
	curl_setopt($http, CURLOPT_HEADER, 0);
	curl_setopt($http, CURLOPT_RETURNTRANSFER, true);
	
	$data =json_decode(curl_exec($http),true);

	$music_data['code'] ='0';
	//单曲标题
	$music_data['title'] =$data['songs'][0]['name'];
	//单曲副标题
	//网易云音乐设置了两个副标题[alia]和[tns] 需要判断哪个有需要的数据
	
	if(@$data['songs'][0]['alia']['0']){
		//如果同时tns存在数据 则真数据在tns
		if(@$data['songs'][0]['tns']['0']){
			$music_data['subtitle'] =$data['songs'][0]['tns']['0'];
		}else{
			$music_data['subtitle'] =$data['songs'][0]['alia']['0'];
		}
	}else if(@$data['songs'][0]['tns']['0']){ //如果alia不存在数据 则真数据在tns
		$music_data['subtitle'] =$data['songs'][0]['tns']['0'];
	}else{ //如果都没有数据subtitle =""
		$music_data['subtitle'] ="";
	}
	
	
	//单曲歌手
	//初始化
	$music_data['singer'] ="";
	
	for($i=0 ;$i <count($data['songs'][0]['ar']) ;$i++){
		$music_data['singer'] =$music_data['singer'].$data['songs'][0]['ar'][$i]['name'];
		//判断是不是最后一名歌手 不是加' / '
		if($i != count($data['songs'][0]['ar'])-1){
			$music_data['singer'] = $music_data['singer'].' / ';
		}
	}
	
	//单曲专辑
	$music_data['album'] =$data['songs'][0]['al']['name'];
	//单曲封面Url
	$music_data['pic'] =$data['songs'][0]['al']['picUrl'];
	
	return $music_data;
}

function musicFM($cookie){
	$http = curl_init();
	curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/personal_fm?"."timestamp=".time());
	curl_setopt($http, CURLOPT_HEADER, 0);
	curl_setopt($http, CURLOPT_POST, 1);
	$header = array('withCredentials: true');
	curl_setopt($http, CURLOPT_COOKIE, $cookie);
	curl_setopt($http, CURLOPT_HTTPHEADER, $header);
	curl_setopt($http, CURLOPT_RETURNTRANSFER, true);
	
	$data =json_decode(curl_exec($http),true);
	$data = $data['data'];
	//初始化playlist
	$music_id['playlist'] =$data[0]['id'];
	for($i =1;$i < count($data);$i++){
		$music_id['playlist'] =$music_id['playlist'].",".$data[$i]['id'];
	}
	$music_id['code'] ="0";
	
	return $music_id; 
}

function musiclike($cookie,$musicID){
	$http = curl_init();
	curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/like?id=".$musicID."&timestamp=".time());
	curl_setopt($http, CURLOPT_HEADER, 0);
	curl_setopt($http, CURLOPT_POST, 1);
	$header = array('withCredentials: true');
	curl_setopt($http, CURLOPT_COOKIE, $cookie);
	curl_setopt($http, CURLOPT_HTTPHEADER, $header);
	//curl_setopt($http, CURLOPT_RETURNTRANSFER, true);
	
	curl_exec($http);
	
}

function similarmusic($MusicID){
	//获取单曲信息
	$http = curl_init();
	curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/simi/song?id=$MusicID");
	curl_setopt($http, CURLOPT_HEADER, 0);
	curl_setopt($http, CURLOPT_RETURNTRANSFER, true);
	
	$data =json_decode(curl_exec($http),true);
	$data = $data['songs'];
	//初始化
	$music_id['playlist'] =$data[0]['id'];
	$music_id['titlet'] =$data[0]['name'];
	$music_id['singer'] ="";
	if(@$data[0]['alias'][0]){
		$music_id['titlet'] = $music_id['titlet']." ".$data[0]['alias'][0];
	}
	for($i =0;$i < count($data[0]['artists']);$i++){
		$music_id['singer'] =$music_id['singer'].$data[0]['artists'][0]['name'];
		//判断是不是最后一名歌手 不是加' / '
		if($i != count($data[0]['artists'])-1){
			$music_id['singer'] = $music_id['singer'].' / ';
		}
	}
	$music_id['singer'] = $music_id['singer'].",";
	
	for($i =1;$i < count($data);$i++){
		$music_id['playlist'] =$music_id['playlist'].",".$data[$i]['id'];
		$music_id['titlet'] =$music_id['titlet'].",".$data[$i]['name'];
		if(@$data[$i]['alias'][0]){
			$music_id['titlet'] = $music_id['titlet']." ".$data[$i]['alias'][0];
		}
		for($e =0;$e < count($data[$i]['artists']);$e++){
			$music_id['singer'] =$music_id['singer'].$data[$i]['artists'][$e]['name'];
			//判断是不是最后一名歌手 不是加' / '
			if($e != count($data[$i]['artists']) -1){
				$music_id['singer'] = $music_id['singer'].' / ';
			}
		}
		$music_id['singer'] = $music_id['singer'].",";
	}
	$music_id['code'] ="0";
	
	return $music_id; 
}

function addmusic($cookie,$musicID,$pid){
	$http = curl_init();
	curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/playlist/tracks?op=add&pid=".$pid."&tracks=".$musicID."&timestamp=".time());
	curl_setopt($http, CURLOPT_HEADER, 0);
	curl_setopt($http, CURLOPT_POST, 1);
	$header = array('withCredentials: true');
	curl_setopt($http, CURLOPT_COOKIE, $cookie);
	curl_setopt($http, CURLOPT_HTTPHEADER, $header);
	//curl_setopt($http, CURLOPT_RETURNTRANSFER, true);
	
	curl_exec($http);
	
}

function fm_trash($cookie,$musicID){
	$http = curl_init();
	curl_setopt($http, CURLOPT_URL, "http://musicapi.leanapp.cn/fm_trash?id=".$musicID);
	curl_setopt($http, CURLOPT_HEADER, 0);
	curl_setopt($http, CURLOPT_POST, 1);
	$header = array('withCredentials: true');
	curl_setopt($http, CURLOPT_COOKIE, $cookie);
	curl_setopt($http, CURLOPT_HTTPHEADER, $header);
	//curl_setopt($http, CURLOPT_RETURNTRANSFER, true);
	
	curl_exec($http);
	
}