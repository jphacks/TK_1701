<?php
//---------------------------
//LINEユーザ情報検索
//---------------------------
	require "database_connect.php";
	$lineuserid=htmlspecialchars($_GET['lineuserid']);//相手のコード
	
//　相手のコードを取り出す
	$stmt_select= $pdo->prepare("SELECT * FROM lineuser WHERE lineuserid=:lineuserid");
	 $stmt_select->bindValue(':lineuserid', $lineuserid, PDO::PARAM_STR);
	 $flag=$stmt_select->execute();
	 if($flag){
	while($result = $stmt_select->fetch(PDO::FETCH_ASSOC)){
        print($result['meepaid']);
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
