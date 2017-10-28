<?php
//---------------------------
//ユーザ情報検索
//---------------------------
	require "database_connect.php";
	$opponent_code=htmlspecialchars($_GET['opponentcode']);//相手のコード
	
//　相手のユーザー名とMACアドレスを取り出す
	$stmt_select= $pdo->prepare("SELECT * FROM User WHERE code=:opponent_code");
	 $stmt_select->bindValue(':opponent_code', $opponent_code, PDO::PARAM_STR);
	 $flag=$stmt_select->execute();
	 if($flag){
	while($result = $stmt_select->fetch(PDO::FETCH_ASSOC)){
        print($result['name'].',');
        print($result['mac']);
        exit();
        }
        //検索がヒットしなかった場合
        print("0");
    exit();
         }else{
                 //データベースに接続失敗した場合
    			print("error");
    			exit();
    		}	
    	exit();
?>
