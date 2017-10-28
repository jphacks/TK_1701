<?php
//---------------------------
//ユーザ情報アップデート&相手のGPS取得
//---------------------------
	require "database_connect.php";
	$code=htmlspecialchars($_GET['code']);//自分のコード
	$opponent_code=htmlspecialchars($_GET['opponentcode']);//相手のコード
	$alt=htmlspecialchars($_GET['alt']);
	$lat=htmlspecialchars($_GET['lat']);
	$lan=htmlspecialchars($_GET['lan']);
	$accuracy=htmlspecialchars($_GET['accuracy']);
	$etime=htmlspecialchars($_GET['etime']);
          $mac=htmlspecialchars($_GET['mac']);
//現在時刻を取得してgettimeに格納
	$date = new DateTime('now', new DateTimeZone('Asia/Tokyo'));
	$gettime= $date->format('Y-m-d H:i:s');
//codeを主キーとし、データを更新
	$stmt = $pdo->prepare("UPDATE User SET alt=:alt,lat=:lat,lan=:lan,
	gettime=:gettime,accuracy=:accuracy,etime=:etime,mac=:mac where code=:value");
			$null=null;
		        $stmt->bindValue(':value', $code, PDO::PARAM_STR);
		        
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
					//登録完了　相手のGPSを取り出す
	$stmt_select= $pdo->prepare("SELECT * FROM User WHERE code=:opponent_code");
	 $stmt_select->bindValue(':opponent_code', $opponent_code, PDO::PARAM_STR);
	 $flag=$stmt_select->execute();
	while($result = $stmt_select->fetch(PDO::FETCH_ASSOC)){
        print($result['name'].',');
        print($result['alt'].',');
        print($result['lat'].',');
        print($result['lan'].',');
          print($result['accuracy'].',');
           print($result['mac'].',');
            print($result['gettime']);
           
    }
    				exit();
			}

?>