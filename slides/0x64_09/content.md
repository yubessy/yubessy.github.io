# 実装 型推論

0x64物語 第09夜 "型"

Shotaro Tanaka

---

Disclaimer

* 社内勉強会用資料です
* 内容には責任を持ちません

---

～前回までのあらすじ～

---

OCamlでOCaml（のサブセット言語）のREPLを実装

```text
#> let rec fact n = if n <= 1 then 1 else n * fact(n - 1)
=> val fact = <fun>

#> fact(10)
=> val - = 3628800
```

---

静的な型システムはまだない

```text
#> let f x = 1 + true
=> val f = <fun>

#> f 1
Fatal error: exception Eval.Error("Both arguments must be integer: +")
```

---

～本編～

---

1. OCamlでOCamlの対話型インタプリタ（REPL）を実装
2. **できたインタプリタに型推論機構を付与**

---

型推論

> 型推論（かたすいろん）とはプログラミング言語の機能の1つで、静的な型付けを持つ言語において、変数や関数の型を宣言しなくてもそれを導くのに使われた関数の型シグネチャなどから自動的に型を決定する機構のこと。
>
> from 型推論 - Wikipedia

---

プリミティブ演算の型推論

```text
#> 1
=> val - : int = 1

#> 1 > 0
=> val - : bool = true
```

* 構文木をたどればできそう

---

関数定義の型推論

```text
#> fun x -> x + 1;;
=> val - : ???
```

* 簡単ではない

---

人間の思考

```ocaml
fun x -> x + 1
```

1. `1` は `int`
2. `+` は `int -> int -> int` だから `x + 1` は `int`
  * ついでに `x` は `int`
3. だからこれは `int -> int`

---

コンピュータのきもち

* 誰が `x + 1` を先に見ろといった？？？
* それ `fun f -> fun g -> f g` の前でも言えんの？

---

ちゃんと（コンピュータにわかるように）やる

---

式・型・型判断

* $e\;$ 式
  * $ e := x \mid n \mid b \mid e_1 \, {\rm op} \, e_2 \mid {\rm if} \, e_1 \, {\rm then} \, e_2 \, {\rm else} \, e_3 $  
    $ \qquad \mid \, {\rm fun} \; x \rightarrow e \mid e_1 \; e_2 $
  * $x\;$ 変数, $n\;$ 整数, $b\;$ 真理値, $\rm op\;$ 二項演算子
* $\gamma\;$ 型
  * $ \gamma := \alpha \, \mid \, {\rm int} \, \mid \, {\rm bool} \, \mid \, \gamma_1 \rightarrow \gamma_2 $
  * $\alpha\;$ 型変数
* $e : \gamma\;$ 型判断（式 $e$ は型 $\gamma$ をもつ）

---

型環境・型付け規則

* $\Gamma\;$ 型環境
  * 変数に対して仮定する型の情報
  * 数学的には変数から型への部分関数
  * 実装上は (変数, 型) ペアのリスト
  * $\Gamma(x) = {\rm int}$ のとき $x : {\rm int}$
* $\Gamma \vdash e : \gamma\;$ 型環境 $\Gamma$ のもとで式 $e$ は型 $\gamma$ をもつ
* 型付け規則

$$
\frac{
  \langle 型判断_1 \rangle , \langle 型判断_2 \rangle , \ldots , \langle 型判断_n \rangle
}{
  \langle 型判断 \rangle
}
$$

---

プリミティブ演算の型付け規則

$$
\frac{
  \Gamma \vdash e_1 : {\rm int} \quad \Gamma \vdash e_2 : {\rm int}
}{
  \Gamma \vdash e_1 + e_2 : {\rm int}
}
$$

* 型付け規則がそのまま型推論アルゴリズムとなる

---

関数定義の型付け規則

$$
\frac{
  \Gamma , x : \gamma_1 \vdash e : \gamma_2
}{
  \Gamma \vdash {\rm fun} \; x \rightarrow e : \gamma_1 \rightarrow \gamma_2
}
$$

引数 $x$ が型 $\gamma_1$ をもつという仮定の下で 関数本体 $e$ が型 $\gamma_2$ をもつならば ${\rm fun} \; x \rightarrow e$ は型
$\gamma_1 \rightarrow \gamma_2$ をもつ"

* $e$ の型を推論する際に $x$ の型が必要となるが、 $x$ の型は未知
* → 単純な推論ができない

---

型推論アルゴリズム

* 型が未知の変数に出くわしたら、とりあえずその型を型変数で表して推論を続ける
* 推論の過程で型変数の実際の型が徐々に明らかになる
* 推論が終わると、型変数とその実際の型の対応関係が得られている
  * この対応関係を **型代入** と呼ぶ

---

型代入

* $\mathcal{S} \;$ 型代入
  * 型変数とその型の対応関係
  * 数学的には型変数から型への有限写像
  * 実装上は (型変数, 型) ペアのリスト
* $\mathcal{S} \gamma \;$ 型 $\gamma$ に含まれる型変数を $\mathcal{S}$ によって置換した型
* $\mathcal{S} \Gamma \;$ 型環境 $\Gamma$ に現れる型に $\mathcal{S}$ を適用して得られる型環境

---

型推論アルゴリズムの入力と出力

* 入力: 型環境 $\Gamma$ と式 $e$
* 出力: $\mathcal{S} \Gamma \vdash e : \gamma$ を結論とする型判断が存在するような $\mathcal{S}$ と $\gamma$

---

型推論アルゴリズムの動作例

```
fun x -> x + 1
```

$$
\frac{
  \Gamma \vdash e_1 : {\rm int} \quad \Gamma \vdash e_2 : {\rm int}
}{
  \Gamma \vdash e_1 + e_2 : {\rm int}
} , \frac{
  \Gamma , x : \gamma_1 \vdash e : \gamma_2
}{
  \Gamma \vdash {\rm fun} \; x \rightarrow e : \gamma_1 \rightarrow \gamma_2
}
$$

1. $x$ の型を $\alpha$ とおく
2. $x + 1$ の両項の型 $\alpha$ , ${\rm int}$ を得る
3. $+$ の型付け規則から
   $x + 1$ の型 ${\rm int}$ と制約 $(\alpha, \, {\rm int})$ を得る
  * $(\alpha, \, {\rm int}) \;$ $\alpha$ と ${\rm int}$ が同じ型を持つ  
4. **単一化** により制約を満たす型代入 $[ \alpha \mapsto {\rm int} ]$ を得る

---

なぜ単一化が必要か

* 3では「制約 $(\alpha, \, {\rm int})$ を得るを得る」と言った
  * 「型代入 $[ \alpha \mapsto {\rm int} ]$ を得る」とは言えないのか？
* → 型付け規則からはあくまで **制約** しか得られない
  * $(\alpha \rightarrow bool, \, {\rm int} \rightarrow \beta \rightarrow \beta)$ のように
    両辺いずれも単一の型変数でない制約が得られることもある
* 制約から型代入を導くのが単一化
  * 制約は「方程式」
  * 型代入は「方程式の解」
  * 単一化は「方程式を解く作業」

---

単一化の形式的定義

与えられた制約の集合

$$
\bigl\{ (\gamma\_{11} , \gamma\_{12}) , (\gamma\_{21} , \gamma\_{22}) , \ldots , (\gamma\_{n1}, \gamma\_{n2}) \bigr\}
$$

に対して

$$
\mathcal{S} \gamma\_{11} = \mathcal{S} \gamma\_{12}, \mathcal{S} \gamma\_{21} = \mathcal{S} \gamma\_{22}, \ldots, \mathcal{S} \gamma\_{n1} = \mathcal{S} \gamma\_{n2}
$$

を満たす型代入 $\mathcal{S}$ を求める

* 単一化問題が解けるかどうかは対象の代数的構造による
* 今回の型システムは **一階の単一化問題**
  → 必ず解が求まる

---

一階の単一化アルゴリズム

$$
TODO
$$

---

実装

```ocaml
 (* (ty * ty) list -> subst *)
let rec unify l = match l with
    [] -> []
  | (ty1, ty2) :: rest when ty1 = ty2 -> unify rest
  | (TyVar n, ty) :: rest -> (* TyVar n implies alpha, ty implies tau, rest implies X *)
      if not (MySet.member n (freevar_ty ty))
      then (n, ty) :: unify (subst_eqs [(n, ty)] rest)
      else err ("Typing failed")
  | (ty, TyVar n) :: rest ->
      if not (MySet.member n (freevar_ty ty))
      then (n, ty) :: unify (subst_eqs [(n, ty)] rest)
      else err ("Typing failed")
  | (TyFun (ty1_1, ty1_2), TyFun (ty2_1, ty2_2)) :: rest ->
      unify ((ty1_1, ty2_1) :: (ty1_2, ty2_2) :: rest)
  | (TyList ty1, TyList ty2) :: rest ->
      unify ((ty1, ty2) :: rest)
  | _ -> err ("Typing failed")
```

---

おさらい

**型推論は方程式を解くようなもの**

* 方程式をたてる
  * 未知の型を型変数でおく
  * 型付け規則を適用することで制約を得る
* 方程式を解く
  * 単一化によって制約から型代入を求める

---

実はまだ話せてないこと：多相性

```
let f = fun x -> x in
  f true && f 1 > 0;;
```

* 同一式の中で `f: bool -> bool` と `f: int -> int` が共存できない

---

力尽きたので詳しく知りたい人はこちらをどうぞ

http://www.fos.kuis.kyoto-u.ac.jp/~t-sekiym/classes/isle4/
