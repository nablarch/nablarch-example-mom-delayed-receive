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
* Java Version : 8
* Maven 3.0.5以降

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

#### 3.3. アプリケーションのビルド
続いて、nablarch-example-mom-delayed-receiveをビルドします。以下のコマンドを実行してください。

    $cd ../nablarch-example-mom-delayed-receive
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

    2016-06-09 09:38:04.784 -INFO- ROO [null] load environment config file. file = classpath:env.config
     INFO | Using Persistence Adapter: MemoryPersistenceAdapter
     INFO | Apache ActiveMQ 5.13.0 (localhost, ID:S1306C00419-T1-25411-1465432685143-0:1) is starting
     INFO | Listening for connections at: tcp://127.0.0.1:61616
     INFO | Connector tcp://127.0.0.1:61616 started
     INFO | Apache ActiveMQ 5.13.0 (localhost, ID:S1306C00419-T1-25411-1465432685143-0:1) started
     INFO | For help or more information please see: http://activemq.apache.org
     WARN | Memory Usage for the Broker (1024 mb) is more than the maximum available for the JVM: 247 mb - resetting to 70% of maximum available: 173 mb
     WARN | Temporary Store limit is 51200 mb (current store usage is 0 mb). The data directory: C:\Users\TIS303995\git\nablarch-example\nablarch-example-mom-delayed-receive only has 6632 mb of usable space - resetting to maximum available disk space: 6632 mb
    2016-06-09 09:38:06.168 -INFO- ROO [null] @@@@ APPLICATION SETTINGS @@@@
        system settings = {
        }
        business date = [20140123]

MOM応答不要メッセージングの送信側を起動すると、MOM応答不要メッセージングの受信側のコンソールに以下のように、メッセージを受信したことと、メッセージを返信したことを示すログが出力されます。
(コンソール中の文字が化けるのは仕様です。)

    2016-06-09 09:58:42.405 -INFO- ROO [201606090958063480006] @@@@ RECEIVED MESSAGE @@@@
            thread_name    = [pool-1-thread-1]
            message_id     = [ID:S1306C00419-T1-20683-1465433922061-1:1:1:1:1]
            destination    = [TEST.REQUEST]
            correlation_id = [null]
            reply_to       = [null]
            message_body   = [ProjectInsertMessage0000000001100                 ?v???W?F?N?g?O?O?P
                                                                                                    development
                                                                                                                        s
                                                                                                                                            20100918201504091        ????
                                                                                                                                                                                         ????
                                                                                                                                                                                                             100      ???l??




                                                          10000    1000     2000     3000           ]

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
