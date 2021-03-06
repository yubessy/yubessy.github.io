class: center, middle

# 良い木とは何か

#### @yubessy

#### 0x64物語 Reboot #09 "木"

---

class: center, middle

# 木

---

class: center, middle

## 0x64物語史上

# 最 小 画 数

---

class: center, middle

#### ぼくのはなし

# 決定木

---

class: center, middle

## 決定木

例: 花火大会

<img src="dt.png" style="width: 80%" />

---

## 決定木

* 決定をするための木
* 説明変数の値から目的変数の値を求める
    * 説明変数: 天気, 風速, 湿度
    * 目的変数: 花火大会を開催するか

#### 決定ステップ

1. １つの説明変数を選択
2. その値に基づいて分岐
3. 1,2を繰り返し、末端に着くと目的変数の値が決定

---

class: center, middle

### 元のデータ

<img src="table.png" style="width: 80%" />

---

class: center, middle

### どうやって作る？

<img src="table.png" style="width: 45%" />
→
<img src="dt.png" style="width: 45%" />

---

class: center, middle

### どっちが良い？

<img src="dt.png" style="width: 48%" />
<img src="dt_another.png" style="width: 48%" />

---

class: center, middle

### そうだ、機械学習しよう

---

## 決定木学習

* 機械学習の手法のひとつ
* 与えられたデータになるべく当てはまる決定木を生成

#### データ

* (説明変数群, 目的変数) の集合

#### モデル

* 入力: 説明変数群の値
* 出力: 目的変数の値

---

## ID3 (Iterative Dichotomiser 3)

* 代表的な決定木学習アルゴリズム
* 各時点で最大の **情報利得** が得られる説明変数を選択
* 選択した説明変数の値による分岐を繰り返すことで木を生成

#### 情報利得

* データ集合 `\(D\)` を、説明変数 `\(X\)` によって分割することで <br />
  目的変数 `\(Y\)` についての不確かさがどれくらい減るか？
  * `\(X\)`: 天気, 湿度, 風速
  * `\(Y\)`: 花火大会
* 情報利得は **エントロピー** を利用して計算できる

---

## エントロピー

* データ集合 `\(D\)` の複雑さ = `\(D\)` の **エントロピー**
* `\(D\)` の異なる種類の要素数が均衡しているほど複雑さが大きい
* `\(P_i\)` : 種類 `\(i\)` の要素が `\(D\)` に占める割合

$$
H(D) = - \sum_{i = 1}^{n} P_i \log_n P_i
$$

* `\( H(😄😄😄😄) = 0 \)`
* `\( H(😄😄😄😰) = -(\frac{3}{4} \log_2 \frac{3}{4} + \frac{1}{4} \log_2 \frac{1}{4}) = 0.81 \)`
* `\( H(😄😄😰😰) = -(\frac{2}{4} \log_2 \frac{2}{4} + \frac{2}{4} \log_2 \frac{2}{4}) = 1 \)`
* `\( H(😄😡😰😰) = -(\frac{1}{4} \log_3 \frac{1}{4} + \frac{1}{4} \log_3 \frac{1}{4} + \frac{2}{4} \log_3 \frac{2}{4}) = 0.94 \)`

---

class: center, middle

### 分割するとエントロピーは？

<img src="gain.png" style="width: 80%" />

---

## 情報利得とエントロピー

* 情報利得 = 分割前後の(平均)エントロピーの差

.center[<img src="gain.png" style="width: 40%" />]

* 分割前: `\(- (\frac{3}{5} \log_2 \frac{3}{5} + \frac{2}{5} \log_2 \frac{2}{5}) = 0.97 \)`
* 分割後: `\(\frac{3}{5} ( - (\frac{2}{3} \log_2 \frac{2}{3} + \frac{1}{3} \log_2 \frac{1}{3})) + \frac{2}{5} (0) = 0.55 \)`
* 風速による情報利得: `\( 0.97 - 0.55 = 0.42 \)`

---

## ID3

* 最大の情報利得が得られる説明変数による分岐を繰り返す
    * (1) 天気 < 湿度 < **風速**
    * (2) 湿度 < **天気**

.center[<img src="dt_another.png" style="width: 70%; center" />]

---

## ID3 pros/cons

#### pros

* 安定（多少データが入れ替わっても木が変わりにくい）
* 高速（計算量が少ない・並列化可能）

#### cons

* 得られる木が最良であるとは限らない
* 属性数の多い変数が選ばれやすい
* 連続値変数にはそのまま適用できない

---

## まとめ

* 頭を使うと良い木ができる

#### 参考

* [決定木の学習](http://www.sist.ac.jp/~kanakubo/research/reasoning_kr/decision_tree.html)
* [情報量 - Wikipedia](https://ja.wikipedia.org/wiki/%E6%83%85%E5%A0%B1%E9%87%8F)
* [カルバック・ライブラー情報量 - Wikipedia](https://ja.wikipedia.org/wiki/%E3%82%AB%E3%83%AB%E3%83%90%E3%83%83%E3%82%AF%E3%83%BB%E3%83%A9%E3%82%A4%E3%83%96%E3%83%A9%E3%83%BC%E6%83%85%E5%A0%B1%E9%87%8F)
