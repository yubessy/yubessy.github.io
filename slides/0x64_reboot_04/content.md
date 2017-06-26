class: center, middle

# あなたの知らない SQLite

@yubessy

0x64物語 Reboot #04 "RDB"

---

class: center, middle

![sqlite](sqlite.gif)

---

# みんな知ってる SQLite

* 軽量RDBMS
  * サーバ不要
  * データは単一ファイルorインメモリ
  * 様々な言語からライブラリとして使える
* RDBMSの基本機能をひと通りもつ
  * 型: NULL, INTEGER, REAL, TEXT, BLOB
  * トランザクション（スレッドセーフ）

---

# もしかしたら知ってる SQLite

* **パブリックドメイン**
* 様々なアプリケーションに組み込まれている
  * iOS(CoreData), Android
  * WebSQL(凍結), IndexedDB
* 実はちゃっかり高機能
  * VIEW
  * TRIGGER
  * TEMP TABLE

---

class: center, middle

#### ここから本編

# あなたの知らない SQLite

---

# WITH RECURSIVE

* MySQLすら8.0まで無かった WITH RECURSIVE
* つまり -> SQLite3 は**チューリング完全**
* 公式ドキュメントより引用
  * https://sqlite.org/lang_with.html

```sql
WITH RECURSIVE
  xaxis(x) AS (VALUES(-2.0) UNION ALL SELECT x+0.05 FROM xaxis WHERE x<1.2),
  yaxis(y) AS (VALUES(-1.0) UNION ALL SELECT y+0.1 FROM yaxis WHERE y<1.0),
  m(iter, cx, cy, x, y) AS (
    SELECT 0, x, y, 0.0, 0.0 FROM xaxis, yaxis
    UNION ALL
    SELECT iter+1, cx, cy, x*x-y*y + cx, 2.0*x*y + cy FROM m
      WHERE (x*x + y*y) < 4.0 AND iter<28
  ),
  m2(iter, cx, cy) AS (
    SELECT max(iter), cx, cy FROM m GROUP BY cx, cy
  ),
  a(t) AS (
    SELECT group_concat( substr(' .+*#', 1+min(iter/7,4), 1), '')
    FROM m2 GROUP BY cy
  )
SELECT group_concat(rtrim(t),x'0a') FROM a;
```

