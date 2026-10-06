# Goal

NTF（Nablarch Testing Framework）のAI対応として、AIが読み書きできないバイナリ形式のExcelテストデータをYAML形式に移行する。
参照: https://github.com/Fintan-contents/nablarch-system-development-guide/pull/211

対象プロジェクト: `nablarch-example-mom-delayed-receive`（MOM応答不要メッセージングの受信側Example）

対象のNTF Excelテストデータ（現時点で1件、`.xlsx`）:
- `src/test/java/com/nablarch/example/ProjectInsertMessageRequestTest.xlsx`

# Acceptance criteria

- `develop` ブランチから作成した `ntf-yaml-support` ブランチで全作業が行われている
- 作業前に既存の全テストがパスすることを確認済みである
- `pom.xml` に `nablarch-testing-yaml:1.0.0-SNAPSHOT` が test スコープの依存として追加されている
- `pom.xml` の `<build><plugins>` に `nablarch-testing-converter:1.0.0-SNAPSHOT` がビルドプラグインとして追加されている（test依存ではない）
- `testDataParser` が参照される全フェーズで YAML が有効になるよう、`YamlTestDataParser` コンポーネントと `yamlInterpreters` リストが適切な設定ファイルに追加されている
- 変換対象の全てのExcelファイルがそれぞれ対応するYAMLファイルに変換されている（サンプリングで一致確認済み）
- 変換後、変換対象だったExcelファイルが全て削除されている
- YAMLテストデータを使った状態で全テストがパスしている

# Assumptions

- `nablarch-testing-yaml:1.0.0-SNAPSHOT` と `nablarch-testing-converter:1.0.0-SNAPSHOT` はローカルMavenリポジトリ（`~/.m2`）に存在する（確認済み）
- `nablarch-example-mom-testing-common:6-NEXT-SNAPSHOT` はローカル `~/.m2` に導入済み（本プロジェクトの runtime 依存。確認済み）
- テスト実行にはH2インメモリDB／組込ActiveMQ Artemisを使用し、DBセットアップ済みである
- YAMLファイルの出力先は `src/test/java` 配下（Excelと同じ場所）とし、`nablarch.test.resource-root` の設定変更は不要
- 変換ツール（`nablarch-testing-converter`）の実行手順は `/home/k10949/workspace/nablarch-testing-converter/README.md` に従う

# Build / Test procedure（README確認結果）

READMEより、本プロジェクトのビルド・テスト手順は以下。

- 前提: 動作確認用モジュール `nablarch-example-mom-testing-common` が `~/.m2` に install 済みであること（本プロジェクトの runtime 依存。導入済みを確認済み）。
- エンティティ生成・DBセットアップは `gsp-dba-maven-plugin` が `generate-sources` フェーズにバインドされており、`mvn test` 実行時に自動で走る（`mvn package` にも含まれる）。README記載の `mvn generate-resources` を個別に実行することも可能。
- テスト実行コマンド: `mvn clean test`
- **ロケール必須**: NTFはメッセージ解決に日本語ロケールを使うため、`LANG=ja_JP.UTF-8 LC_ALL=ja_JP.UTF-8` を付与して実行する（`C.UTF-8` だとメッセージ解決に失敗してテストが落ちる）。
- 実行例: `LANG=ja_JP.UTF-8 LC_ALL=ja_JP.UTF-8 mvn clean test`

実行環境: JDK 21 / Maven 3.9.16 / H2 2.2.220 / ActiveMQ Artemis 2.37.0。

# Rules

- commit every change; one completion marker per task
- push は行わない（コミットのみ。リモートへの push は最後にユーザーが実施）
- テストクラス（`.java`）は変更しない
- `develop` から新規作成した `ntf-yaml-support` ブランチで作業する
- steering.md は各タスクで更新し、当該タスクのコミットに含める
- テストはExcelファイルが存在しない状態で最終パスを確認する
- YAML変換後はサンプリングでExcelとYAMLの内容を比較してから削除する
- サンプリング比較で差異が見つかった場合は修正せずユーザーに報告して停止する
- `YamlTestDataParser` / `yamlInterpreters` の配置は「テスト実行中に `testDataParser` が参照される全フェーズで YAML リーダーが有効になる」ように決める（配置判定は Decisions 参照）

# Tasks

### #1: 現状のビルド・テストで全PASSを確認する

**Purpose**: `develop` から `ntf-yaml-support` ブランチを作成し、変更前の状態で全テストがパスすることを確認する。

**Prerequisites**: none

**Steps**:

- [x] `develop` ブランチの存在を確認し、そこから `ntf-yaml-support` ブランチを新規作成する
- [x] 対象プロジェクトの README を確認し、ビルド・テストの実行手順を把握する
- [x] 隣接プロジェクトの `.rn/` 配下MDを参照し、steering.md と scripts の初版を作成する
- [x] README で確認した手順でテストを実行し、全テストがパスすることを確認する（`mvn clean test`。gspによるエンティティ生成はtest実行に内包）
- [x] 結果を `checks/task-1.md` に記録する（self-check列のみ）
- [x] 変更ファイル（check ファイルおよび steering.md）をコミットする
- [x] self-check (OK/NG per completion criterion, record in checks/task-1.md)
- [x] QA expert review (Agent: general-purpose / Opus 4.8) — Overall Verdict: OK
- [ ] user review（順調のため全task完了後にまとめて実施）

**Completion criteria**:

- `mvn test` が全テストパスで終了している（BUILD SUCCESS）

### #2: 設定変更 — pom.xml と unit-test.xml にYAML対応を追加

**Purpose**: `pom.xml` に `nablarch-testing-yaml`（test 依存）を追加し、`nablarch-testing-converter` を `<build><plugins>` のビルドプラグインとして追加し、`unit-test.xml` に `yamlInterpreters` リストと `YamlTestDataParser` コンポーネントの設定を追加する。

**Prerequisites**: #1

**Steps**:

- [x] `pom.xml` に `nablarch-testing-yaml:1.0.0-SNAPSHOT`（test scope）を追加する
- [x] `pom.xml` の `<build><plugins>` に `nablarch-testing-converter:1.0.0-SNAPSHOT` をビルドプラグインとして追加する（`<configuration>` に from=xls / to=yaml / input / output / overwrite を設定）
- [x] 配置判定（Decisions参照）に従い `yamlInterpreters` と `YamlTestDataParser`（`testDataParser` 上書き）を `unit-test.xml` に配置する
- [x] `mvn dependency:resolve` / `mvn help:describe` で yaml依存・converterプラグインの `convert` ゴールが解決できることを確認する
- [x] 変更したファイルをコミットする
- [x] self-check (OK/NG per completion criterion, record in checks/task-2.md)
- [ ] QA expert review (Agent: general-purpose / Opus 4.8)
- [ ] language expert review (Agent: general-purpose / Opus 4.8)
- [ ] software-engineering expert review (Agent: general-purpose / Opus 4.8)
- [ ] user review（順調のため全task完了後にまとめて実施）

**Completion criteria**:

- `pom.xml` に `nablarch-testing-yaml:1.0.0-SNAPSHOT` が test スコープで追加されている
- `pom.xml` の `<build><plugins>` に `nablarch-testing-converter:1.0.0-SNAPSHOT` がビルドプラグインとして追加されている
- `yamlInterpreters` リストと `testDataParser`（`YamlTestDataParser`）が、`testDataParser` が参照される全フェーズで有効になる設定ファイル（`unit-test.xml`）に追加されている
- `mvn dependency:resolve` が成功し、converter プラグインの `convert` ゴールが `mvn help:describe` で解決できる

### #3: ExcelテストデータをYAMLに変換

**Purpose**: `nablarch-testing-converter` を使って ExcelのテストデータをYAML形式に変換し、ソースツリー内に配置して、変換内容をサンプリングで確認する。

**Prerequisites**: #2

**Steps**:

- [ ] 対象 Excel ファイルの所在を確認する
- [ ] 同名の `.xls` と `.xlsx` が併存していないか確認する（本プロジェクトは `.xlsx` 1件のみで併存なし）
- [ ] README の手順に従い、全ての対象ExcelファイルをYAML変換する
- [ ] 変換後、各YAMLファイルについてExcelの内容とサンプリングで比較し、一致を確認する（各ファイル最低3件）
- [ ] サンプリング比較結果を `checks/task-3.md` に記録する（差異があれば報告・停止）
- [ ] 変換したYAMLファイルをコミットする（Excelはまだ削除しない）
- [ ] self-check (OK/NG per completion criterion, record in checks/task-3.md)
- [ ] QA expert review (Agent: general-purpose / Opus 4.8)
- [ ] user review

**Completion criteria**:

- 変換対象の全てのExcelファイルに対応するYAMLファイルが生成されている
- サンプリング比較（各ファイル最低3件、元データが3件未満の場合は元データ件数分）でExcelとYAMLの内容が一致している
- 変換後のYAMLファイルがコミットされている

### #4: Excelを削除する

**Purpose**: 変換対象だった Excel を削除してコミットする。

**Prerequisites**: #3

**Steps**:

- [ ] 変換対象となった Excel ファイルを削除する
- [ ] 削除結果（残存Excelが0件であること）を `checks/task-4.md` に記録する
- [ ] Excelファイル削除をコミットする
- [ ] self-check (OK/NG per completion criterion, record in checks/task-4.md)
- [ ] QA expert review (Agent: general-purpose / Opus 4.8)
- [ ] user review

**Completion criteria**:

- 変換対象だった全 Excel ファイルが削除されている
- 削除がコミットされている

### #5: YAMLテストデータで全テストをパスさせる

**Purpose**: Excelなしで全テストがパスすることを確認する。

**Prerequisites**: #4

**Steps**:

- [ ] `mvn test` が BUILD SUCCESS になることを確認する
- [ ] テストログで YamlTestDataParser が実際に使われていることを確認する
- [ ] self-check (OK/NG per completion criterion, record in checks/task-5.md)
- [ ] QA expert review (Agent: general-purpose / Opus 4.8)
- [ ] language expert review (Agent: general-purpose / Opus 4.8)
- [ ] software-engineering expert review (Agent: general-purpose / Opus 4.8)
- [ ] user review

**Completion criteria**:

- `mvn test` が BUILD SUCCESS で終了する（全テストパス、Excel ファイルなし）
- テストログで YamlTestDataParser が実際に使われていることが確認できる

# Decisions

- **YamlTestDataParser / yamlInterpreters の配置先 = `unit-test.xml` のみ**（`messaging-async-receive-boot.xml` には入れない）。
  - 根拠（NTFソース `nablarch-testing` を精査して判定）: 本テストは `@MessagingReceiveTest`（`MessagingReceiveTestSupport`）による応答不要の受信テスト。実行フローは次の通りで、`testDataParser` が参照されるのは全て unit-test.xml リポジトリ有効時のみ。
    - `beforeExecute` の `setUpMessages`（`MESSAGE=setUpMessages`）読込 → unit-test.xml 下
    - `TestShot.setUp()` の `setUpTable`（`SETUP_TABLE`）読込 → unit-test.xml 下
    - アクション実行（`MainForRequestTesting`）は boot.xml でリポジトリ再初期化するが、受信電文は**組込キューから**受け取り、`testDataParser` は参照しない
    - `MainForRequestTesting.handle()` は `finally` で `revertDefaultRepository()` を呼びリポジトリを unit-test.xml に復帰 → 以後の `assertTables()`（`EXPECTED_TABLE`）読込は unit-test.xml 下
  - Excelデータに `RESPONSE_*` ブロックは無く、同期送信（sendSync）系ではない → Rules の「非該当（unit-test.xml だけで足りる）」ケースに合致。
  - 配置順: `unit-test.xml` 内で `nablarch/test/test-data.xml` の import より後、かつ `dbInfo` 定義より後に置き、参照解決（`nullInterpreter` 等の component-ref、`dbInfo` ref）を担保する。

# State

- **Status**: in progress
- **Date**: 2026-10-06
- **Last completed**: #2 設定変更（self-check OK、レビュー実施中）
- **Next**: #2 の3エキスパートレビュー → #3 YAML変換
- **Notes**: ベースライン `mvn clean test` = BUILD SUCCESS（Tests run: 1, Failures: 0, Errors: 0, Skipped: 0）。user review は順調時まとめて実施方針。
