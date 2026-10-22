# nablarch-example-mom-delayed-receive NTF YAML 対応 steering

## Overview
- 目的・背景: NTF（Nablarch Testing Framework）のAI対応として、AIが読み書きできないバイナリ形式のExcelテストデータをYAML形式に移行する（背景資料: https://github.com/Fintan-contents/nablarch-system-development-guide/pull/211）。
- Acceptance criteria 要約:
  - 既存 `ntf-yaml-support` ブランチの履歴（過去の移行コミット `996b356`〜`ca0dd1f`）を維持したまま、その先端の上に develop 一致ツリーのベースラインコミットを積み、task-1〜5 を**再実施**する（スキップ禁止）
  - ベースライン（develop 同等）で全テストPASSを確認
  - `pom.xml` に `nablarch-testing-yaml:1.0.0-SNAPSHOT`（test）と `nablarch-testing-converter:1.0.0-SNAPSHOT`（`<build><plugins>` のビルドプラグイン）を追加
  - `YamlTestDataParser`（`testDataParser`）を `testDataParser` が参照される全フェーズで有効な設定ファイルに追加（`interpreters` は `dateTimeInterpreter` → `compositeInterpreter` の2つのみ）
  - 全テスト用ExcelをYAMLへ変換（サンプリング一致確認）→ Excel削除 → YAMLで全テストPASS
- 対象プロジェクト: `nablarch-example-mom-delayed-receive`（MOM応答不要メッセージングの受信側Example）
- 対象Excel（ベースライン時点）: `src/test/java/com/nablarch/example/ProjectInsertMessageRequestTest.xlsx`（1件）
- 指示書: `.rn/ntf-yaml-support/ntf-yaml-support-instructions_mom-delayed-receive_V1.6.md`（原本: `/home/k10949/instructions/ntf-yaml-support-instructions_mom-delayed-receive_V1.6.md`）

## Build / Test
README で確認した手順:
- 前提: `nablarch-example-mom-testing-common`（runtime 依存）が `~/.m2` に install 済みであること（`~/.m2/repository/com/nablarch/example/nablarch-example-mom-testing-common` の存在を確認済み）。
- プロファイル指定: 不要（README に `-P gsp` 等の記載なし）。
- エンティティ生成・DBセットアップ: `gsp-dba-maven-plugin` が `generate-sources` フェーズにバインドされており（generate-ddl / execute-ddl / load-data）、`mvn test` / `mvn package` 実行時に自動で走る。README記載の `mvn generate-resources` で個別実行も可能。
- テスト実行コマンド: `mvn clean test`（エンティティ生成を内包）
- ロケール: NTF のメッセージ解決のため日本語ロケールで実行する（本環境は `LANG=ja_JP.UTF-8 LC_ALL=ja_JP.UTF-8` が既定）。
- 実行環境: JDK 21.0.2 / Maven 3.9.16

## Tasks

### #1: ベースライン化（developと同一状態）し、全PASSを確認する
**Purpose**: 既存の `ntf-yaml-support` ブランチ（履歴維持）上で、過去の移行コミットの有無にかかわらず、先端の上にワーキングツリー全体を `develop` と同一内容へ揃える新規コミットを1つ積み、その状態で全テストがパスすることを確認する（過去の完了済み成果があっても再実施）。
**Prerequisites**: none
**Steps**:
- [x] 既存の `ntf-yaml-support` ブランチへ切り替える（新規作成なし。履歴維持）
- [x] 過去の移行コミット（`996b356`〜`ca0dd1f`）を巻き戻さず、先端 `ca0dd1f` の上に処理を積む
- [x] `develop` の存在を確認し、`git read-tree -u --reset develop` → `git commit` でベースラインコミット `0dfe07e` を積む（`HEAD^{tree}` = `develop^{tree}` = `17600658...`）
- [x] README を確認し、ビルド・テスト手順を把握する（本書「Build / Test」）
- [x] steering.md と scripts の初版を作成する（本指示書のMDテンプレートに準拠）
- [x] 本指示書を `.rn/ntf-yaml-support/` 配下へ同名でコピーし、`cmp` で原本と同一であることを確認する
- [x] README の手順に従いエンティティクラスを生成する（`mvn clean generate-resources` → 成功）
- [x] `mvn test` で全テストがパスすることを確認する（Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 / BUILD SUCCESS）
- [x] 結果を `checks/task-1.md` に記録する（self-check列）
- [x] 変更ファイル（check ファイル、steering.md、指示書のコピー、scripts）をコミットする
- [x] self-check (OK/NG per completion criterion, record in checks/task-1.md)
- [x] QA expert review (Agent: general-purpose / Opus) — OK（軽微指摘: task-1.md に script 目的を追記→対応済み）
- [ ] user review（全task完了後にまとめて実施）
**Completion criteria**:
- 既存の `ntf-yaml-support` ブランチ上で作業しており、コミット履歴が維持されている（再作成・rebase・amend・hard reset なし。過去の移行コミットが残っている）
- ブランチの既存先端の上に、ワーキングツリー全体が `develop` と同一内容になる新規コミットが1つ積まれている
- `mvn test` が全テストパスで終了している（BUILD SUCCESS）
- 本指示書のコピーが `.rn/ntf-yaml-support/` 配下に原本と同一内容で配置され、コミットされている

### #2: 設定変更 — pom.xml と unit-test.xml にYAML対応を追加
**Purpose**: `pom.xml` に `nablarch-testing-yaml`（test 依存）と `nablarch-testing-converter`（ビルドプラグイン。plugin直下 `<configuration>` に変換パラメータ、CLIで `convert` ゴール実行）を追加し、`YamlTestDataParser`（`testDataParser`）を Rules の「全フェーズで有効化」方針に従い配置する。`interpreters` は testdata.html の設定例どおり。
**Prerequisites**: #1
**Steps**:
- [x] `pom.xml` に `nablarch-testing-yaml:1.0.0-SNAPSHOT`（test scope）を追加する
- [x] `pom.xml` の `<build><plugins>` に `nablarch-testing-converter:1.0.0-SNAPSHOT` を追加する（`<configuration>` に from=xls / to=yaml / input / output / overwrite。web/batch 準拠）
- [x] 実行中にモック等が `testDataParser` 経由でテストデータ（応答電文など）を読む構成かを判定する → 非該当（`RESPONSE_*` なし・sendSync なしの応答不要受信テスト。setUpMessages/SETUP/EXPECTED の読込はいずれも unit-test.xml 有効時）
- [x] 判定結果に応じて `YamlTestDataParser` を配置する → `src/test/resources/unit-test.xml`（`interpreters` は `dateTimeInterpreter` → `compositeInterpreter` の2つのみ。`test-data.xml` import・`dbInfo` 定義より後）
- [x] `mvn dependency:resolve` / `mvn help:describe -Dplugin=com.nablarch.framework:nablarch-testing-converter:1.0.0-SNAPSHOT` で解決を確認する
- [x] 変更したファイルをコミットする
- [x] self-check (OK/NG per completion criterion, record in checks/task-2.md)
- [x] QA expert review (Agent: general-purpose / Opus) — OK
- [x] language expert review (Agent: general-purpose / Opus) — OK
- [x] software-engineering expert review (Agent: general-purpose / Opus) — OK（軽微所見: SNAPSHOT 直書き2か所。web/batch 準拠のため現状維持）
- [ ] user review（全task完了後にまとめて実施）
**Completion criteria**:
- `pom.xml` に `nablarch-testing-yaml:1.0.0-SNAPSHOT` が test スコープで追加されている
- `pom.xml` の `<build><plugins>` に `nablarch-testing-converter:1.0.0-SNAPSHOT` がビルドプラグインとして追加されている
- `YamlTestDataParser` が `testDataParser` が参照される全フェーズで有効になる設定ファイルに追加され、`interpreters` に `dateTimeInterpreter` → `compositeInterpreter` の2つのみが記載されている
- `mvn dependency:resolve` が成功し、converter プラグインの `convert` ゴールが `mvn help:describe` で解決できる

### #3: ExcelテストデータをYAMLに変換
**Purpose**: `nablarch-testing-converter` で ExcelテストデータをYAMLへ変換し、ソースツリー内に配置して、サンプリングで内容を確認する。
**Prerequisites**: #2
**Steps**:
- [x] 対象 Excel ファイルの所在を確認する（`ProjectInsertMessageRequestTest.xlsx` 1件）
- [x] 同名の `.xls` と `.xlsx` の併存有無を確認する（併存なし）
- [x] converter README の手順に従い、全対象ExcelをYAML変換する（`mvn com.nablarch.framework:nablarch-testing-converter:1.0.0-SNAPSHOT:convert` → Converted 1 file(s)）
- [x] 各YAMLファイルについてExcelとサンプリング比較する（全ブロック全件 100件、NG 0）
- [x] 比較結果を `checks/task-3.md` の「Sampling Comparison」に記録する
- [x] 変換したYAMLファイルをコミットする（Excelはまだ削除しない）
- [x] self-check (OK/NG per completion criterion, record in checks/task-3.md)
- [x] QA expert review (Agent: general-purpose / Opus) — OK（差異なし）
- [ ] user review（全task完了後にまとめて実施）
**Completion criteria**:
- 変換対象の全てのExcelファイルに対応するYAMLファイルが生成されている
- サンプリング比較でExcelとYAMLの内容が一致している
- 変換後のYAMLファイルがコミットされている

### #4: Excelを削除する
**Purpose**: 変換対象だった Excel を削除してコミットする。
**Prerequisites**: #3
**Steps**:
- [x] 変換対象となった Excel ファイルを削除する（`ProjectInsertMessageRequestTest.xlsx`）
- [x] 削除結果（残存Excel 0件）を `checks/task-4.md` に記録する
- [x] Excelファイル削除をコミットする
- [x] self-check (OK/NG per completion criterion, record in checks/task-4.md)
- [x] QA expert review (Agent: general-purpose / Opus) — OK
- [ ] user review（全task完了後にまとめて実施）
**Completion criteria**:
- 変換対象だった全 Excel ファイルが削除されている
- 削除がコミットされている

### #5: YAMLテストデータで全テストをパスさせる
**Purpose**: Excelなしで全テストがパスすることを確認する。
**Prerequisites**: #4
**Steps**:
- [x] `mvn clean test` が BUILD SUCCESS になることを確認する（Excel なし、Tests run: 1, Failures: 0）
- [x] テストログで YamlTestDataParser が実際に使われていることを確認する（上書き WARN ＋ YamlTestDataParser INFO ＋ YAML 値によるテストショット実行）
- [x] self-check (OK/NG per completion criterion, record in checks/task-5.md)
- [x] QA expert review (Agent: general-purpose / Opus) — OK（変異テストで YAML の有効性を裏付け）
- [x] language expert review (Agent: general-purpose / Opus) — OK
- [x] software-engineering expert review (Agent: general-purpose / Opus) — OK
- [ ] user review（全task完了後にまとめて実施）
**Completion criteria**:
- `mvn test` が BUILD SUCCESS で終了する（全テストパス、Excel ファイルなし）
- テストログで YamlTestDataParser が実際に使われていることが確認できる

## Scripts
- `scripts/dump_xlsx_grid.py` — 標準ライブラリ（zipfile + xml.etree）のみで `.xlsx` の各シートをセルグリッド（セル参照→値）として出力する。openpyxl 非導入環境でも、task-3 の Excel↔YAML サンプリング比較で元Excel側の値を正確に読むためのスクリプト。使い方: `python3 -I .rn/ntf-yaml-support/scripts/dump_xlsx_grid.py <xlsx>`（task-3 でふりがな `<rPh>` を値から除外するよう修正）
- `scripts/compare_xlsx_yaml.py` — task-3 のサンプリング比較用。Excel の各データブロック（LIST_MAP / SETUP_TABLE / MESSAGE / EXPECTED_TABLE）の値と変換後 YAML の値を突合し TSV で一致/不一致を出力する（結果: `checks/task-3-compare.tsv`）。使い方: `python3 -I .rn/ntf-yaml-support/scripts/compare_xlsx_yaml.py <xlsx> <yaml-dir>`

## Decisions
- `YamlTestDataParser` の配置先 = `src/test/resources/unit-test.xml` のみ。本テストは `@MessagingReceiveTest` の応答不要受信テストで、Excel に `RESPONSE_*` ブロックがなく sendSync もないため、アクション実行中に `testDataParser` は参照されない（Rules の「非該当」ケース）。本番用設定（`src/main/resources`）・`messaging-async-receive-boot.xml` は変更しない。
- 対象ファイルの改行コード（CRLF）は維持する。
- `ProjectInsertMessageRequestTest/setUpDb.yaml`（0バイト）は元 Excel の空シート `setUpDb` を converter がそのまま変換したもの。元データとの1対1対応を保つため残置する（NTF の YamlLoader は空ファイルを空 Map として扱い、テストからは参照されない）。

## State
- Status: #1〜#5 完了（全レビュー OK）。user review（まとめて実施）待ち。push はユーザーが実施
- Notes: 過去の移行コミット（`996b356`〜`ca0dd1f`）は維持。ベースラインコミット `0dfe07e` の上に再実施分を積む。push はユーザーが最後に実施。
