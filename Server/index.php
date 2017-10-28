<?php
//セッションの開始
session_start();
require_once __DIR__ . '/vendor/autoload.php';
 
$httpClient = new \LINE\LINEBot\HTTPClient\CurlHTTPClient(getenv('CHANNEL_ACCESS_TOKEN'));
$bot = new \LINE\LINEBot($httpClient, ['channelSecret' => getenv('CHANNEL_SECRET')]);


$signature = $_SERVER["HTTP_" . \LINE\LINEBot\Constant\HTTPHeader::LINE_SIGNATURE];
 $json_string = file_get_contents('php://input');
    $jsonObj = json_decode($json_string);
    $userId = $jsonObj->{"events"}[0]->{"source"}->{"userId"};
    
try {
  $events = $bot->parseEventRequest(file_get_contents('php://input'), $signature);
} catch(\LINE\LINEBot\Exception\InvalidSignatureException $e) {
  error_log("parseEventRequest failed. InvalidSignatureException => ".var_export($e, true));
} catch(\LINE\LINEBot\Exception\UnknownEventTypeException $e) {
  error_log("parseEventRequest failed. UnknownEventTypeException => ".var_export($e, true));
} catch(\LINE\LINEBot\Exception\UnknownMessageTypeException $e) {
  error_log("parseEventRequest failed. UnknownMessageTypeException => ".var_export($e, true));
} catch(\LINE\LINEBot\Exception\InvalidEventRequestException $e) {
  error_log("parseEventRequest failed. InvalidEventRequestException => ".var_export($e, true));
}

foreach ($events as $event) {
  if (!($event instanceof \LINE\LINEBot\Event\MessageEvent)) {
    error_log('Non message event has come');
    continue;
  }
  error_log("Type()".$event->getMessageType());
  
if($event->getMessageType()=="location"){

          replyMultiMessage($bot, $event->getReplyToken(),
    new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("位置を登録できませんでした"),
     new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("test"));

    /*
    $date = new DateTime('now', new DateTimeZone('Asia/Tokyo'));
	$gettime= $date->format('Y-m-d H:i:s');
    //位置情報登録
    $result_location = file_get_contents('https://noor-ubermensch.ssl-lolipop.jp/enPiT/update_user.php?code='. $_SESSION['code'].'&alt=0&lat='.$event->getLatitude().'&lan='.$event->getLongitude().'&accuracy=0&etime='.$gettime);
    if($result_location==0){//更新エラー
      replyMultiMessage($bot, $event->getReplyToken(),
    new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("位置を登録できませんでした"),
     new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("".$_SESSION['code']));
    }else{
          replyMultiMessage($bot, $event->getReplyToken(),
    new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("位置を登録しました。以下のコードを相手に教えてください"),
     new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("".$_SESSION['code']));
    }

*/

}else{
	$result = file_get_contents('https://noor-ubermensch.ssl-lolipop.jp/enPiT/regist_user.php?name='. $event->getText() . '&mac=');
	  error_log("result:".$result);
	     //ユーザー登録失敗
    if($result=="0"){
         replyMultiMessage($bot, $event->getReplyToken(),
    new \LINE\LINEBot\MessageBuilder\TextMessageBuilder($event->getText()."さんの登録を行います"),
     new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("登録に失敗しました"));

    }else{//ユーザ登録成功
   
         replyMultiMessage($bot, $event->getReplyToken(),
    new \LINE\LINEBot\MessageBuilder\TextMessageBuilder($userId ."さんの登録を行います"),
     new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("登録に成功しました"),
      new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("あなたのコードは以下です。"),
      new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("$result"),
        new \LINE\LINEBot\MessageBuilder\TextMessageBuilder("次に位置情報を送ってください")  );

    }
	}
   }


function replyLocationMessage($bot, $replyToken, $title, $address, $lat, $lon) {
  $response = $bot->replyMessage($replyToken, new \LINE\LINEBot\MessageBuilder\LocationMessageBuilder($title, $address, $lat, $lon));
  if (!$response->isSucceeded()) {
    error_log('Failed!'. $response->getHTTPStatus . ' ' . $response->getRawBody());
  }
}

function replyTextMessage($bot, $replyToken, $text) {
  $response = $bot->replyMessage($replyToken, new \LINE\LINEBot\MessageBuilder\TextMessageBuilder($text));
  if (!$response->isSucceeded()) {
    error_log('Failed!'. $response->getHTTPStatus . ' ' . $response->getRawBody());
  }
}
function replyMultiMessage($bot, $replyToken, ...$msgs) {
  $builder = new \LINE\LINEBot\MessageBuilder\MultiMessageBuilder();
  foreach($msgs as $value) {
    $builder->add($value);
  }
  $response = $bot->replyMessage($replyToken, $builder);
  if (!$response->isSucceeded()) {
    error_log('Failed!'. $response->getHTTPStatus . ' ' . $response->getRawBody());
  }
}
function replyConfirmTemplate($bot, $replyToken, $alternativeText, $text, ...$actions) {
  $actionArray = array();
  foreach($actions as $value) {
    array_push($actionArray, $value);
  }
  $builder = new \LINE\LINEBot\MessageBuilder\TemplateMessageBuilder(
    $alternativeText,
    new \LINE\LINEBot\MessageBuilder\TemplateBuilder\ConfirmTemplateBuilder ($text, $actionArray)
  );
  $response = $bot->replyMessage($replyToken, $builder);
  if (!$response->isSucceeded()) {
    error_log('Failed!'. $response->getHTTPStatus . ' ' . $response->getRawBody());
  }
}





 ?>