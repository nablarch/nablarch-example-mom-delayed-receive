#!/usr/bin/env python3
"""
compare_xlsx_yaml.py — NTF テストデータ Excel と変換後 YAML のサンプリング比較（task-3）。

目的: ProjectInsertMessageRequestTest.xlsx の各シートのデータブロック
      （LIST_MAP / SETUP_TABLE / MESSAGE / EXPECTED_TABLE）について、
      Excel のセル値と converter が出力した YAML の値を突き合わせ、一致/不一致を表形式で出力する。
      Excel 側は dump_xlsx_grid.py（同ディレクトリ）でふりがなを除いたセル値を読む。

使い方: python3 -I compare_xlsx_yaml.py <xlsx> <yaml-dir>
        <yaml-dir> は <テストクラス名>/ ディレクトリ（シート名.yaml を格納）。
出力:   「シート\tブロック\tキー\tExcelセル\tExcel値\tYAML値\t一致」の TSV。最終行に件数集計。
前提:   PyYAML が利用可能であること。
"""
import os
import re
import sys

import yaml

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import dump_xlsx_grid as grid  # noqa: E402

MARKER = re.compile(r"^(LIST_MAP|SETUP_TABLE|MESSAGE|EXPECTED_TABLE)(\[(\w+)\])?=(.+)$")


def col_name(idx):
    s = ""
    idx += 1
    while idx:
        idx, r = divmod(idx - 1, 26)
        s = chr(ord("A") + r) + s
    return s


def read_sheets(path):
    """シート名 -> {(row, col): value} を返す。"""
    import io
    import contextlib
    buf = io.StringIO()
    with contextlib.redirect_stdout(buf):
        grid.main(path)
    sheets, cur = {}, None
    for line in buf.getvalue().splitlines():
        m = re.match(r"^===== SHEET: (.*) =====$", line)
        if m:
            cur = sheets.setdefault(m.group(1), {})
            continue
        if line == "(empty)" or "\t" not in line:
            continue
        ref, v = line.split("\t", 1)
        cur[(grid.row_of(ref), grid.col_to_idx(ref))] = eval(v)  # repr 文字列を戻す
    return sheets


def row_values(cells, r):
    return {c: v for (rr, c), v in cells.items() if rr == r}


def is_comment(v):
    return v.startswith("//")


def blocks(cells):
    """マーカー行からブロック（種別, グループ, 名前, マーカー行）を列挙する。"""
    out = []
    for (r, c), v in sorted(cells.items()):
        if c == 0:
            m = MARKER.match(v)
            if m:
                out.append((m.group(1), m.group(3), m.group(4), r))
    return out


def data_rows(cells, start, max_row):
    """マーカー行の次から、コメント行を飛ばしてヘッダ行・データ行を返す（空行で終了）。"""
    rows = []
    r = start + 1
    while r <= max_row:
        vals = row_values(cells, r)
        if not vals:
            break
        if not is_comment(vals.get(0, "")):
            rows.append((r, vals))
        r += 1
    return rows


def emit(res, sheet, block, key, ref, xv, yv):
    ok = "OK" if xv == yv else "NG"
    res.append((sheet, block, key, ref, xv, yv, ok))


def compare(xlsx, ydir):
    res = []
    for sheet, cells in read_sheets(xlsx).items():
        ypath = os.path.join(ydir, sheet + ".yaml")
        with open(ypath, encoding="utf-8") as f:
            # BaseLoader: YAML 1.1 の暗黙型変換（キー `no` → False 等）を避け、全て文字列で読む
            doc = yaml.load(f, Loader=yaml.BaseLoader) or {}
        if not cells:
            emit(res, sheet, "(sheet)", "empty", "-", "(empty)", "(empty)" if not doc else repr(doc))
            continue
        max_row = max(r for r, _ in cells)
        for kind, group, name, mrow in blocks(cells):
            rows = data_rows(cells, mrow, max_row)
            if kind in ("LIST_MAP", "SETUP_TABLE", "EXPECTED_TABLE"):
                (hr, header), body = rows[0], rows[1:]
                if kind == "LIST_MAP":
                    yb = next(b for b in doc["list_maps"] if b["id"] == name)
                elif kind == "SETUP_TABLE":
                    yb = next(b for b in doc["setup_tables"] if b["table"] == name)
                else:
                    yb = next(b for b in doc["expected_tables"]
                              if b["table"] == name and str(b.get("group_id")) == str(group))
                label = f"{kind}{'[' + group + ']' if group else ''}={name}"
                emit(res, sheet, label, "row count", f"{hr + 1}-", str(len(body)), str(len(yb["rows"])))
                for i, (r, vals) in enumerate(body):
                    yrow = yb["rows"][i] if i < len(yb["rows"]) else {}
                    for c, col in sorted(header.items()):
                        emit(res, sheet, label, f"row{i + 1}.{col}", f"{col_name(c)}{r}",
                             vals.get(c, ""), str(yrow.get(col, "<missing>")))
            else:  # MESSAGE
                ym = next(b for b in doc["messages"] if b["id"] == name)
                label = f"MESSAGE={name}"
                # ディレクティブ / FW ヘッダ（2列のキー・値行）→ フィールド名行 → 型行 → 長さ行 → データ行
                kv = [(r, v) for r, v in rows if len(v) == 2 and 1 in v and not v[0].isdigit() and v[0] != "no"]
                for r, v in kv:
                    k, xv = v[0], v[1]
                    yv = ym["directives"].get(k, ym.get("fw_header", {}).get(k, "<missing>"))
                    emit(res, sheet, label, k, f"B{r}", xv, str(yv))
                rest = [x for x in rows if x not in kv]
                (fr, names), (tr, types), (lr, lens), body = rest[0], rest[1], rest[2], rest[3:]
                rec = ym["records"][0]
                for c in sorted(k for k in names if k > 0):
                    f = rec["fields"][c - 1]
                    emit(res, sheet, label, f"field{c}.name", f"{col_name(c)}{fr}", names[c], f["name"])
                    emit(res, sheet, label, f"field{c}.type", f"{col_name(c)}{tr}", types.get(c, ""), f["type"])
                    emit(res, sheet, label, f"field{c}.length", f"{col_name(c)}{lr}", lens.get(c, ""), f["length"])
                emit(res, sheet, label, "row count", f"A{lr + 1}-", str(len(body)), str(len(rec["rows"])))
                for i, (r, vals) in enumerate(body):
                    for c in sorted(k for k in names if k > 0):
                        emit(res, sheet, label, f"row{i + 1}.{names[c]}", f"{col_name(c)}{r}",
                             vals.get(c, ""), rec["rows"][i][c - 1])
    return res


def main(xlsx, ydir):
    res = compare(xlsx, ydir)
    print("sheet\tblock\tkey\tcell\texcel\tyaml\tmatch")
    for r in res:
        print("\t".join(map(str, r)))
    ng = sum(1 for r in res if r[-1] == "NG")
    print(f"# total={len(res)} OK={len(res) - ng} NG={ng}")
    return 1 if ng else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1], sys.argv[2]))
