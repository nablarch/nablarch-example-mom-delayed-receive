#!/usr/bin/env python3
"""
dump_xlsx_grid.py — 依存ライブラリなしで .xlsx の各シートをセルグリッドとして出力する。

目的: NTF テストデータ Excel（ProjectInsertMessageRequestTest.xlsx）を YAML 変換した結果と
      元 Excel の内容をサンプリング突合（task-3）するための、元 Excel 側の正確な読み取り手段。
      openpyxl が無い環境向けに、標準ライブラリ（zipfile + xml.etree）だけで実装している。

使い方: python3 -I dump_xlsx_grid.py <path-to-xlsx>
出力:   シートごとに「<セル参照>\t<値>」を行順・列順で出力する。
"""
import sys
import zipfile
import xml.etree.ElementTree as ET

NS = "{http://schemas.openxmlformats.org/spreadsheetml/2006/main}"
PKG = "{http://schemas.openxmlformats.org/package/2006/relationships}"


def text_of(el):
    """<si> / <is> 配下のテキストを連結する（ふりがな <rPh> は除外し、表示上の基底テキストのみ）。"""
    parts = []
    for child in el:
        if child.tag == f"{NS}t":
            parts.append(child.text or "")
        elif child.tag == f"{NS}r":
            parts.extend(t.text or "" for t in child.iter(f"{NS}t"))
    return "".join(parts)


def col_to_idx(ref):
    """セル参照 'B12' の列文字 'B' を 0 始まりの列番号に変換する。"""
    letters = "".join(ch for ch in ref if ch.isalpha())
    idx = 0
    for ch in letters:
        idx = idx * 26 + (ord(ch) - ord("A") + 1)
    return idx - 1


def row_of(ref):
    return int("".join(ch for ch in ref if ch.isdigit()))


def main(path):
    z = zipfile.ZipFile(path)

    # 共有文字列
    shared = []
    if "xl/sharedStrings.xml" in z.namelist():
        root = ET.fromstring(z.read("xl/sharedStrings.xml"))
        for si in root.findall(f"{NS}si"):
            # リッチテキスト対応。ふりがな（<rPh>）は値ではないため除外する
            shared.append(text_of(si))

    # シート名と対応するファイル
    wb = ET.fromstring(z.read("xl/workbook.xml"))
    rels = ET.fromstring(z.read("xl/_rels/workbook.xml.rels"))
    rid_to_target = {r.get("Id"): r.get("Target") for r in rels}
    sheets = []
    for s in wb.find(f"{NS}sheets"):
        rid = s.get(f"{{http://schemas.openxmlformats.org/officeDocument/2006/relationships}}id")
        target = rid_to_target[rid]
        if not target.startswith("xl/"):
            target = "xl/" + target
        sheets.append((s.get("name"), target))

    for name, target in sheets:
        print(f"===== SHEET: {name} =====")
        sheet = ET.fromstring(z.read(target))
        data = sheet.find(f"{NS}sheetData")
        if data is None:
            print("(empty)")
            continue
        any_cell = False
        for row in data.findall(f"{NS}row"):
            for c in row.findall(f"{NS}c"):
                ref = c.get("r")
                t = c.get("t")
                v = None
                if t == "inlineStr":
                    is_el = c.find(f"{NS}is")
                    if is_el is not None:
                        v = text_of(is_el)
                else:
                    v_el = c.find(f"{NS}v")
                    if v_el is not None:
                        v = v_el.text
                        if t == "s":
                            v = shared[int(v)]
                if v is not None:
                    any_cell = True
                    print(f"{ref}\t{v!r}")
        if not any_cell:
            print("(empty)")


if __name__ == "__main__":
    main(sys.argv[1])
