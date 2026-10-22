# task-3 Completion Check

## Completion Criteria

| Criterion | Self-check | Evidence | QA | QA Evidence |
|---|---|---|---|---|
| 変換対象の全てのExcelファイルに対応するYAMLファイルが生成されている | OK | 対象Excelは `git ls-files` / `find`（target・work 除外）で `src/test/java/com/nablarch/example/ProjectInsertMessageRequestTest.xlsx` の1件のみ。`mvn com.nablarch.framework:nablarch-testing-converter:1.0.0-SNAPSHOT:convert`（pom の `<configuration>` 使用）→ `Converted 1 file(s).` / BUILD SUCCESS。2シート（setUpDb / testNormalEnd）に対応する `ProjectInsertMessageRequestTest/setUpDb.yaml`（空シート→0バイト）・`testNormalEnd.yaml` を生成 OK | target/・work/ 除外の `find`・`git ls-files` で Excel は1件、同名 `.xls` なし。workbook のシート2つ（setUpDb/testNormalEnd）に各 YAML あり。setUpDb はセル値0件で、NTF YamlLoader は0バイト YAML を emptyMap として扱う |
| サンプリング比較でExcelとYAMLの内容が一致している | OK | `scripts/compare_xlsx_yaml.py` で全ブロックの全データ値（各ブロック3件以上。元データ3件未満のブロックは全件）を比較し total=100 / OK=100 / NG=0（結果: `checks/task-3-compare.tsv`）。差異なし OK | 独自ダンプ（rPh 除外）から期待 YAML 構造を全件組み立て、`BaseLoader` で読んだ testNormalEnd.yaml と構造全体が完全一致。B4/A11/B27/P31/R39 等を個別突合し一致。`compare_xlsx_yaml.py` 再実行結果は tsv とバイト一致。差異なし |
| 変換後のYAMLファイルがコミットされている | OK | YAML 2ファイルを task-3 コミットに含める（Excel は未削除） OK | `git ls-files` に YAML 2件と xlsx が存在（xlsx は HEAD とワーキングツリーで md5 一致＝未削除）。`__pycache__` は `280a4f5` で除去済み |

## Sampling Comparison（task-3 のみ）
| YAML ファイル | 比較件数 | レコードキー/行 | Excel 値 | YAML 値 | 一致 |
| --- | --- | --- | --- | --- | --- |
| `ProjectInsertMessageRequestTest/setUpDb.yaml` | 全件（元データ0件。空シート） | シート全体 | (empty) | (empty, 0バイト) | OK |
| `ProjectInsertMessageRequestTest/testNormalEnd.yaml` | LIST_MAP=testShots 全件（1行×8列） | row1.no (A4) | 1 | 1 | OK |
| 同上 | 同上 | row1.description (B4) | 正常系電文 | 正常系電文 | OK |
| 同上 | 同上 | row1.diConfig (F4) | messaging-async-receive-boot.xml | messaging-async-receive-boot.xml | OK |
| 同上 | SETUP_TABLE=ID_GENERATE 全件（1行×2列） | row1.GENERATOR_ID (A11) / GENERATED_NO (B11) | 9991 / 0 | 9991 / 0 | OK |
| 同上 | SETUP_TABLE=INS_PROJECT_RECEIVE_MESSAGE 全件（0行） | 行数 | 0 | 0（`rows: []`） | OK |
| 同上 | MESSAGE=setUpMessages 全件（ディレクティブ/ヘッダ3、フィールド定義15×3、データ1行×15列） | text-encoding (B25) / requestId (B26) / userId (B27) | MS932 / ProjectInsertMessage / 0000000101 | MS932 / ProjectInsertMessage / 0000000101 | OK |
| 同上 | 同上 | row1.プロジェクト名 (B31) / 売上高 (L31) / FILLER2 (P31) | プロジェクト００１ / 10000 / (空) | プロジェクト００１ / 10000 / "" | OK |
| 同上 | EXPECTED_TABLE[1]=INS_PROJECT_RECEIVE_MESSAGE 全件（1行×21列） | row1.RECEIVED_MESSAGE_SEQUENCE (A39) / PROJECT_START_DATE (E39) / INSERT_DATE (R39) | 0000000001 / 2010-09-18 / 2014-09-13 12:34:56.0 | 0000000001 / 2010-09-18 / 2014-09-13 12:34:56.0 | OK |

（上表は代表値。全100件の比較結果は `checks/task-3-compare.tsv`。差異なし）

補足（差異ではない事項）:
- YAML の `directives.file-type: "Fixed"` は Excel に明示セルがないが、NTF は MESSAGE ブロックを固定長として読む（converter `TestCoreReaderAdapter.java:165` の `FixedLengthFileParser`）。暗黙値の明示化であり内容の差異ではない。
- Excel セルのふりがな（rPh。例: `正常系電文セイジョウケイデンブン`）は値ではないため比較対象外。converter も基底テキストのみ出力。
- `SETUP_TABLE=INS_PROJECT_RECEIVE_MESSAGE` はヘッダ（`RECEIVED_MESSAGE_SEQUENCE`）のみでデータ行0件。YAML は `rows: []`（テーブルを空にする意味は同一）。
- 参考: 生成 YAML は過去の移行コミット `ca0dd1f` の YAML とバイト一致。

## .xls/.xlsx 併存チェック（該当時のみ）
- 併存の有無: なし（`.xlsx` 1件のみ、同名 `.xls` なし）
- 差分確認結果の保存先: 該当なし
- 未使用側（通常 `.xlsx`）の削除: 該当なし

## Scripts
- `scripts/dump_xlsx_grid.py` — `.xlsx` をセルグリッドで出力（本 task でふりがな `<rPh>` を値から除外するよう修正）
- `scripts/compare_xlsx_yaml.py` — Excel の各データブロックと YAML の値を突合し TSV 出力（YAML は `BaseLoader` で全て文字列として読む。`safe_load` だとキー `no` が真偽値 `False` になるため）

## QA Expert Review

| Aspect | Verdict | Evidence / Improvement |
|---|---|---|
| Meaningful tests/verification | OK | 全セル値を比較。値改変の変異（PROJECT_LEADER→`佐藤X`）を NG として検出。NTF 側は snakeyaml-engine（YAML 1.2）でキー `no` の真偽値化も起きないことを確認 |
| Edge case coverage | OK | 空シート・データ0行 SETUP_TABLE・空セル（FILLER2）・コメント行・rPh を網羅。改善余地（任意）: 比較スクリプトは Excel→YAML の片方向で YAML 側のみの余剰キー/ブロックは未検出、ブロック終端判定が空行基準（NTF は次マーカーまで）。今回データでは影響なし（逆方向の構造一致は QA が確認済み） |

## Expert Reviews (code changes only)

N/A — no code changes in this task.

## Overall Verdict

- Self-check: OK
- QA: OK（差異なし。比較スクリプトの双方向化は任意の改善提案）
- Language expert: N/A
- Software-engineering expert: N/A
- Ready for user review: Yes
