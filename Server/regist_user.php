<?php
//-------------------
//ユーザ登録
//-------------------
	require "database_connect.php";
	
	$name=htmlspecialchars($_GET['name']);
	$mac=htmlspecialchars($_GET['mac']);
	$alt=0;
	$lat=0;
	$lan=0;
	$gettime=0;
	$accuracy=0;
	$etime=0;
//現在時刻を取得してgettimeに格納
	$date = new DateTime('now', new DateTimeZone('Asia/Tokyo'));
	$gettime= $date->format('Y-m-d H:i:s');
//ID発行4文字ランダム.被る可能性はある
$code=substr(str_shuffle('1234567890abcdefghijklmnopqrstuvwxyz'), 0, 4);

	$stmt = $pdo->prepare("INSERT INTO User (id,code,name,alt,lat,lan,gettime,accuracy,etime,mac)VALUES (:id,:code,:name,:alt,:lat,:lan,:gettime,:accuracy,:etime,:mac)");
			$null=null;
			$stmt->bindParam(':id', $null, PDO::PARAM_NULL);
			$stmt->bindValue(':code', $code, PDO::PARAM_STR);
			$stmt->bindParam(':name', $name, PDO::PARAM_STR);
			$stmt->bindParam(':alt', $alt, PDO::PARAM_STR);
			$stmt->bindParam(':lat', $lat, PDO::PARAM_STR);
			$stmt->bindValue(':lan', $lan, PDO::PARAM_STR);
			$stmt->bindValue(':gettime', $gettime, PDO::PARAM_STR);
			$stmt->bindValue(':accuracy', $accuracy, PDO::PARAM_STR);
			$stmt->bindValue(':etime', $etime, PDO::PARAM_STR);
			$stmt->bindValue(':mac', $mac, PDO::PARAM_STR);
			$flag=$stmt->execute();
				if(!$flag){
					//データ送信エラー
					echo"0";
					exit();
				}else{
					//登録完了
					echo $code;
					exit();
			}

?>