# MeePa:動画デモ
(下の画像クリックでyoutubeに飛びます)

[![MeePa](https://user-images.githubusercontent.com/32015564/32144461-9ee9b6d2-bcfc-11e7-8d8c-0706a5fda57d.png)](https://youtu.be/qxzyXHlVpKA)

## 製品概要
### 会いたい気持ち × Tech = MeePa

### エレベーターピッチ
![エレベータピッチ](https://user-images.githubusercontent.com/32015564/32144469-bdbce8c2-bcfc-11e7-80c2-d5ac27248caf.png)


[ 混雑した待ち合わせ場所で相手が見つからない ] を解決したい  
[ 待ち合わせで困った経験のある人] 向けの  
[ 体感的&視覚的にわかりやすく”会いたい気持ち”をサポート]する[ MeePa ] です。  
これは [AR/レーダ機能/視点共有で視覚的に相手のいる方向がわかり、振動機能で画面を見なくても相手の距離がわかり、WIFIダイレクトでオフライン使用のサポート] ができ、  
[ 既存の目的地が場所である (Waaaaay!,MapFan AR Global,端末レーダー)]とは違って、  
[ 目的地が人で、集合場所にたどり着いてからのサポート機能] が備わっています。  


[スライド](https://www.slideshare.net/secret/o6G9lGOeYamIPz)


### 背景（製品開発のきっかけ、課題等）
**集合場所に来たのに，待ち合わせ相手が見つからない！**
なんてことはありませんか？  
![02](https://user-images.githubusercontent.com/32015564/32144477-dcc7dc18-bcfc-11e7-904c-fc55b6210da8.png)

特に，  
* 相手の顔が見にくい(花火大会，ライブ会場)
* 人がたくさん集まるところ(ショッピングモール)
* 始めて訪れる場所(駅，旅行先での別行動した後の集合)  

などでは，一発で待ち合わせ相手を見つけることが難しく  
その後の予定に影響が出てしまうことも...  
なかなか会えないことでフラストレーションもたまります．

【待ち合わせで困った経験のある人】にとって  
【集合場所についてから相手に会うまで】に生じる  
【電話やSNSでメッセージをやりとりしないと会えない】問題の解決が求められています．  

### 製品説明（具体的な製品の説明）
MeePaはあなたの"会いたい気持ち"をサポートするAndroidアプリです．

![06](https://user-images.githubusercontent.com/32015564/32144488-fd156148-bcfc-11e7-901a-f281ab957096.png)


### 特長

![04](https://user-images.githubusercontent.com/32015564/32144482-f7ddab68-bcfc-11e7-92fb-3cc0018719fb.png)

#### 1. 相手の位置・方向をわかりやすく可視化
レーダ/ARによって相手の位置が視覚的にわかりやすい
相手が見える景色を共有されることで位置把握がより簡単になる

#### 2. 相手との距離が振動パターンでわかる
振動パターンの変化で，相手との距離が画面を見なくてもわかる

#### 3. オフライン通信機能
Wifi Directによりオフラインでも端末間で直接位置情報を交換可能


### 競合他社との違い
![05](https://user-images.githubusercontent.com/32015564/32144487-fce33010-bcfc-11e7-858b-bb9660f28344.png)

### 解決出来ること
待ち合わせ相手に会うまでの作業・時間短縮  


### 今後の展望
リリース

## 開発内容・開発技術
### 活用した技術
![07](https://user-images.githubusercontent.com/32015564/32144489-fd46eb50-bcfc-11e7-9b27-b3208bbd0508.png)
![08](https://user-images.githubusercontent.com/32015564/32144490-fd75b304-bcfc-11e7-8a74-ae91d96396b2.png)
![09](https://user-images.githubusercontent.com/32015564/32144491-fda6b382-bcfc-11e7-885b-81d2f0d47ab1.png)

### 実証
![1](https://user-images.githubusercontent.com/32015564/32144535-9754af52-bcfd-11e7-8ee6-bb3f919b90ed.png)
![2](https://user-images.githubusercontent.com/32015564/32144536-97812f00-bcfd-11e7-8f67-a0426fb469e3.png)
![3](https://user-images.githubusercontent.com/32015564/32144537-97c113cc-bcfd-11e7-9a37-74308149e526.png)
![4](https://user-images.githubusercontent.com/32015564/32144538-97ed7412-bcfd-11e7-91a5-01b4f530960f.png)
![5](https://user-images.githubusercontent.com/32015564/32144539-981bef72-bcfd-11e7-8a6d-7c85196051cd.png)
![6](https://user-images.githubusercontent.com/32015564/32144540-9848ad46-bcfd-11e7-8606-7d2c8c7e9b14.png)
![7](https://user-images.githubusercontent.com/32015564/32144541-9875fcba-bcfd-11e7-9025-4502d8453220.png)

#### API・データ
##### スポンサーからの提供技術
* WebRTCプラットフォーム「SkyWay」:視点共有機能のため


#### フレームワーク・ライブラリ・モジュール
* OpenGL ES2.0：ARオブジェクト描画のため
* SDK of the LINE Messaging API：Line Botのため
* FusedLocationProviderApi：位置情報取得のため

#### デバイス
* Nexus7 Android Ver.6.0.1
* Nexus9 Android Ver.6.0
* XperiaZ3 Android Ver.5.0.2
* アクオスゼータ Android Ver.5.0.2

### 研究内容・事前開発プロダクト（任意）
#### チーム実績
enPiT 筑波大学分野ワークショップ2016 最優秀/enPiT BizAppBizSysD 分野ワークショップ2016 最優秀/PBL summit2017 みんなのウェディング賞

#### 諸星智也(筑波大学大学院)：プロジェクトリーダー
筑波大学大学院システム情報工学研究科コンピュータサイエンス専攻長特別表彰/JPHACKS innovator認定/JBS日本システムデザインセンター賞/新日鉄住金ソリューションズ賞/サイボウズ賞/enPit 最優秀賞/ミュージックマンシップ vol.8 準優勝
[http://www.tomoyamorohoshi.com :Portforio]

#### 北村拓也(広島大学大学院)：サーバーサイドエンジニア
教育をよりパーソナライズに、よりインタラクティブに、よりゲーム的にするために活動中/アプリ開発会社Ubermensch(株) 代表取締役/プログラミングスクール (株)テックチャンス 取締役/学習工学研究室D1/広島大学起業部 第一期学生代表/IPA 未踏クリエイター/NICT SecHack365 trainee/U-22プログラミングコンテスト CSAJ会長賞/セキュリティキャンプアワード 優秀賞/人工知能学会研究会優秀賞/星新一賞 最終候補作など、他27件受賞

#### 伊与部美咲(茨城大学大学院)：クライアントエンジニア
趣味はポケモンに変な名前をつけること。あとコイルを捕まえたい。
ムーミンではムーミンママが好き。ムーミンは肩が無いから嫌い。
#### 鏑木かおり(富山大学大学院)：クライアントエンジニア
生まれ変わったら水族館のお姉さんになりたい。
#### 青木海(筑波大学大学院)：テクニカルディレクター
未踏スーパークリエータ'15/FPGA設計,高位合成コンパイラ開発/作曲家


### 独自開発技術（Hack Dayで開発したもの）
#### 2日間に開発した独自の機能・技術
* AndroidSDKから取得したGPSを使っていたが、精度が悪かったので、FusedLocationProviderApiを導入した
* LINEBOTを使った位置情報共有
* SkyWayによる視点共有
