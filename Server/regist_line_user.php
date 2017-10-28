<?php
//-------------------
//ラインユーザ登録
//-------------------
	require "database_connect.php";
	
	$lineuserid=htmlspecialchars($_GET['lineuserid']);
	$meepaid=htmlspecialchars($_GET['meepaid']);

	$stmt = $pdo->prepare("INSERT INTO lineuser (id,lineuserid,meepaid)
	VALUES (:id,:lineuserid,:meepaid)");
			$null=null;
			$stmt->bindParam(':id', $null, PDO::PARAM_NULL);
			$stmt->bindValue(':lineuserid', $lineuserid, PDO::PARAM_STR);
			$stmt->bindParam(':meepaid', $meepaid, PDO::PARAM_STR);
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