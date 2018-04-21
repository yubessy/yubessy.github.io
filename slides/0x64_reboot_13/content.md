<!-- $theme: gaia -->

### Resource Polymorphism

##### @yubessy

#### 0x64物語 Reboot #13

#### "排他|mutex|ownership"

---

##### ※今日の話は ownership 寄りです

---

### 2018/03/07

#### arXiv.org に突如現れた論文が(その手の)業界を席巻！

---

![](rp.png)

---

### Resource polymorphism

Author: Guillaume Munch-Maccagnoni (Inria)

> We present a resource-management model for ML-style programming languages, designed to be compatible with the OCaml philosophy and runtime model. ... It builds on the ownership-and-borrowing models of systems programming languages (Cyclone, C++11, Rust) and on linear types in functional programming (Linear Lisp, Clean, Alms).

OCamlのランタイムGCに、Rustに代表される所有・借用モデルを導入！？

---

### Resource

プログラミング言語理論において
**リソース** = コピーや廃棄にコストがかかる値

* (複雑な)構造体
* ファイルハンドラ
* ソケット
* ロック
* ...

これらの組み合わせで構成される値もリソース

---

### Resouce management

リソースはプログラムの至るところで生成される

もしリソースを適切に管理しなければ...？
→ ヒープ領域の圧迫によりプログラムが簡単にOOMで死ぬ

モダンな言語はたいてい**リソース管理**(RM)の仕組みを持つ

---

### Resouce management

RMの方式として代表的なもの

#### Garbage Collection

**実行中**に随時不要なリソースを検出・破棄する
Java, Go など多くの言語が採用

#### Ownership and Borrowing

**実行前**にプログラムを解析してリソース破棄処理を挿入する
Rust の他、一部の実験的なFP言語で linear type として採用

---

### Why resource polymorphism ?

GC, OBの一長一短

#### Garbage Collection

* pros: プログラマがメモリ管理をしなくてよい
* cons: ランタイムの肥大化, STW

#### Ownership and Borrowing

* pros: ランタイムが小さい, STWなし
* cons: 複雑な参照が扱いにくい

---

### Why resource polymorphism ?

理想: 同じ言語の中で併用したい

* 単純な値 → OBでコンパイル時に解決
* 複雑な値 → GCで自動管理

現実: RMはだいたい言語依存

* RM方式は言語の初期設計段階で決定
* あとからRM方式を変えるのは困難

→ 言語の限界 = RMの限界という誤解
e.g. Rust vs. Go

---

### Why resource polymorphism ?

言語研究者としてはこの問題をなんとかしたい

ヒント: リソースは値 & 値には型がある
アイデア: 値の型によってRM方式を選択できないか？

* GC型の値はGCで管理
* OB型の値はOBで管理

---

### Resource polymorphism explained

実現への課題:

* 異なる方式を同一ランタイム上で効率的に扱えるか？
* 方式の異なる型同士の複合型をどう扱うか？

Resource Polymorphism はこれを現実的に解決する
以降はほんの触りだけを紹介

---

### Resource polymorphism explained

３つの基本型を導入

* G型: GCで管理
* O型: ownership モデルで管理
* B型: borrowing モデルで管理

基本型だけなら同一ランタイム内で使い分けるのもそう難しくない

* O型, B型はコンパイル時に破棄処理を挿入
* G型のみGCで管理

---

### Resource polymorphism explained

問題は異なる基本型同士の複合型

* 参照
* 直積, 直和
* タプル, リスト, ツリー, ...

複合型は無限に存在 → 個別に扱いを定義できない

しかし、実は複合型は規則によって単純化して扱える！

---

### Resource polymorphism explained

![](gob.png)

例えばG型とO型の直積 `G*O` はO型として処理できる
= `G*O` の値もコンパイル時に破棄処理を挿入してよい

特にO, B型と何らかの型の複合型からG型が生まれることはない
= GCが必要になる箇所をかなり減らすことができる

これが(たぶん)一番のポイント

---

### 論文は実装例含めてまだまだ続くが・・・

---

### まとめ

* GCとOBの併用は人類の夢
* Resource Polymorphism は型システムでこれを解決
* GC言語に無理なくOBを導入できるとも捉えられる
* あと5年くらいしたらトレンドになってるはず
