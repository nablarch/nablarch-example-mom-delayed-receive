nablarch-example-mom-delayed-receive
====================================

Nablarch FrameworkのMOM応答不要メッセージングの受信側のExampleです。
MOM応答不要メッセージングの送信側のExampleと組み合わせて使用します。

以下にメッセージングのシステムのうち、本Exampleが対象とする箇所を示します。

![概要](./fig/abstract.png "概要")

※図中の「オンライン処理アプリケーション等」と「バッチアプリケーション」の部分はExampleの提供はありません。

## 実行手順

### 1.動作環境
実行環境に以下のソフトウェアがインストールされている事を前提とします。
* Java Version : 17
* Maven 3.9.0以降

補足：
MOMとRDBMSはExampleに組み込まれたものを使用します。

### 2. プロジェクトリポジトリの取得
Gitを使用している場合、アプリケーションを配置したいディレクトリにて「git clone」コマンドを実行してください。
以下、コマンドの例です。

    $mkdir c:\example
    $cd c:\example
    $git clone https://github.com/nablarch/nablarch-example-mom-testing-common.git
    $git clone https://github.com/nablarch/nablarch-example-mom-delayed-receive.git

Gitを使用しない場合、最新のタグからzipをダウンロードし、任意のディレクトリへ展開してください。

### 3. アプリケーションのビルド
#### 3.1. 動作確認用モジュールのビルド
まず、nablarch-example-mom-testing-commonをビルドします。

    $cd nablarch-example-mom-testing-common
    $mvn clean install

#### 3.2. データベースのセットアップ及びエンティティクラスの作成
続いて、データベースのセットアップ及びエンティティクラスの作成を行います。以下のコマンドを実行してください。

    $cd ../nablarch-example-mom-delayed-receive
    $mvn clean generate-resources

#### 3.3. アプリケーションのビルド
次に、nablarch-example-mom-delayed-receiveをビルドします。以下のコマンドを実行してください。

    $mvn package

### 4. アプリケーションの起動

以下のコマンドで、データベースの状態を最新化、MOM応答不要メッセージングの受信側のExampleが起動します。

    $mvn generate-resources
    $mvn exec:java -Dexec.mainClass=nablarch.fw.launcher.Main -Dexec.args="'-diConfig' 'messaging-async-receive-boot.xml' '-requestPath' 'RECEIVEAPP' '-userId' 'batch_user'"

なお、 `maven-assembly-plugin` を使用して実行可能jarの生成を行っているため、以下の手順にて実行することもできます。

1. ``target/application-<version_no>.zip`` を任意のディレクトリに解凍する。
2. 以下のコマンドにて実行する

    $java -jar <1で解凍したディレクトリ名>/nablarch-example-mom-delayed-receive-<version_no>.jar -diConfig classpath:messaging-async-receive-boot.xml -requestPath RECEIVEAPP -userId batch_user

起動に成功すると以下のようなログがコンソールに出力され、MOM応答不要メッセージングの送信側からのメッセージの受信待ちの状態になります。

```log
2023-02-15 14:45:13.642 -INFO- nablarch.fw.launcher.Main [null] boot_proc = [] proc_sys = [mom-delayed-recei
ve] req_id = [null] usr_id = [null] @@@@ APPLICATION SETTINGS @@@@
        system settings = {
        }
        business date = [20140123]
```

MOM応答不要メッセージングの送信側を起動すると、MOM応答不要メッセージングの受信側のコンソールに以下のように、メッセージを受信したことと、メッセージを返信したことを示すログが出力されます。
(コンソール中の文字が化けるのは仕様です。)

```log
2023-02-15 14:46:39.456 -INFO- MESSAGING [202302151445136440001] boot_proc = [] proc_sys = [mom-delayed-receive] req_
id = [RECEIVEAPP] usr_id = [batch_user] @@@@ RECEIVED MESSAGE @@@@
        thread_name    = [pool-1-thread-1]
        message_id     = [ID:1e07ab35-acf4-11ed-8482-9c7befbbf589]
        destination    = [TEST.REQUEST]
        correlation_id = [null]
        reply_to       = [null]
        message_body   = [ProjectInsertMessage0000000001                    ?v???W?F?N?g?O?O?P

                                                                                                  development
                                                                                                             s

   20100918201504091        ????

                                                  ????

                                                                        100      ???l??








                                                    10000    1000     2000     3000       ]
```


自動的に終了はしないため、ctrl + c等で終了させてください。

### 5. DBの確認方法

1. https://www.h2database.com/html/download.html からH2をインストールしてください。  

2. {インストールフォルダ}/bin/h2.bat を実行してください(コマンドプロンプトが開く)。  
  ※h2.bat実行中はExampleアプリケーションからDBへアクセスすることができないため、Exampleアプリケーションを停止しておいてください。

3. ブラウザから http://localhost:8082 を開き、以下の情報でH2コンソールにログインしてください。
   JDBC URLの{dbファイルのパス}には、`SAMPLE.h2.db`ファイルの格納ディレクトリまでのパスを指定してください。  
  JDBC URL：jdbc:h2:{dbファイルのパス}/SAMPLE  
  ユーザ名：SAMPLE  
  パスワード：SAMPLE
