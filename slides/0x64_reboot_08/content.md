<!-- $theme: gaia -->

### Type Erasure と Reflection のはなし

##### @yubessy

#### 0x64物語 Reboot #08

#### "静的型付き言語"

---

### "静的型付き言語" とは？

* 静的型付け機構をもつ処理系で処理される言語
* 言語が静的型付きかは**構文ではなく処理系**による
* たとえば以下のようなことも可能
    * Javaプログラムを静的型付けなしで実行
    * Pythonプログラムに静的型付けを適用

---

### "静的型付け" とは？

* プログラムの**実行前**に変数や関数の型を決める
* プログラムの実行前 ≠ コンパイル時
    * 例: mypy = 型検査のみでコンパイルはしない
* とはいえ大抵はコンパイル時に型検査も行う

---

### 静的型付けのメリット

* めんどくさい？

```java
Map<String, String> listA = new HashMap<String, String>()
```

* むずかしい？
    * ジェネリクス, 代数的データ型, ...
* 本来のメリット
    * **プログラムの実行前にエラーを検出**
    * 「刀を抜く前に勝負をつける」！

---

#### ※語りだすと長くなるので省略

---

# あれ？

###### もしかして実行時に型は要らない・・・？

---

### 実行時に型は必要？

* 実は必要ないケースも多い
* 型検査が成功 ⇒ 実行時に型エラーが起こらない
	* 型システムの**健全性**と呼ばれる (≠ 完全性)
    * 語りだすと長く(ry
* 型検査を通れば実行時に型情報は必要ない
    * 「当たらなければどうということはない」！
* 実際いくつかの言語では**Type Erasure**を行う

---

### Type Erasure (型消去)

* コンパイル時にプログラムから型情報を取り除く
    * オーバーヘッドを軽減する
    * 多相性を手軽に実現する
* Java, Scala
    * Type Erasureによりジェネリクスを実現
    * JVMの後方互換性を保てるのがメリット

---

#### 例: Javaのジェネリクス

```java
List<String> list = new ArrayList<String>();
list.add("string");
String str = list.get(0);
```

↓ 型消去後のイメージ

```java
List list = new ArrayList();
list.add("string");
String str = (String) list.get(0);
```

---

#### 例: Javaのジェネリクス

```java
List<String> list = new ArrayList<String>();
list.add("string");
String str = list.get(0);
```

```java
   0  new java.util.ArrayList [15]
   3  dup
   4  invokespecial java.util.ArrayList() [17]
   7  astore_1 [list]
   8  aload_1 [list]
   9  ldc <String "string"> [18]
  11  invokeinterface java.util.List.add(java.lang.Object) : boolean [20] [nargs: 2]
  16  pop
  17  aload_1 [list]
  18  iconst_0
  19  invokeinterface java.util.List.get(int) : java.lang.Object [26] [nargs: 2]
  24  checkcast java.lang.String [30]
  27  astore_2 [str]
```

---

### Again: 実行時に型は必要？

* もちろん必要な場合もある
    * 人間のためのランタイムデバッグ
    * データが与えられないと型が決まらない場合
        * JSONのパース
* Java, Scala での解決策
	* **Reflection**により実行時に型情報を取得

---

### Reflection (自己言及)

* プログラムが自身のメタデータを参照すること
    * Runtime Reflection
    * Compile Time Reflection
    * Reification (Reflectionによる抽象構文木の操作)
* (ry
* 要するに
    * Reflectionがあれば実行時に型情報を得られる

---

#### 例: ScalaのTypeTag

```scala
import scala.reflect.runtime.universe._

// implicit ... によりコンパイラがTypeTagを生成
// =「あとでここの型情報を使うから覚えておいてね」
def paramInfo[T](x: T)(implicit tag: TypeTag[T]) = {
  val targs = tag.tpe match {
    case TypeRef(_, _, args) => args
  }
  println(s"type of $x has type arguments $targs")
}
```

```scala
scala> paramInfo(42)
type of 42 has type arguments List()

scala> paramInfo(List(1, 2))
type of List(1, 2) has type arguments List(Int)
```

---

#### 例: ScalaのTypeTag

```scala
def paramInfo[T](x: T)(implicit tag: TypeTag[T]) = {
  val targs = tag.tpe match {
    case TypeRef(_, _, args) => args
  }
  println(s"type of $x has type arguments $targs")
}
```

↓context boundを使うと略記できる

```scala
def paramInfo[T: TypeTag](x: T) = {
  val targs = typeOf[T] match {
    case TypeRef(_, _, args) => args
  }
  println(s"type of $x has type arguments $targs")
}
```

---

### おまけ: 型推論と型消去

* 実は型消去は**型推論**と対になる概念
* Scalaでの型の一生
    1. 人間は一部の型しか書かない
    2. **型推論**で頑張って型をつける
    3. 型検査で型エラーがないことを確認
    4. **型消去**でつけたばかりの型を消す
    5. 一部の情報はリフレクションで記憶・参照

---

### まとめ

![](pikmin.png)

* 型のいのちはとてもはかない
* 嫌わずかわいがってあげましょう
    * コンパイル遅いとか言わない

---

### 参考

* [Generics（Java）の型消去について - sinsengumi血風録](http://sinsengumi.net/blog/2011/12/generics%EF%BC%88java%EF%BC%89%E3%81%AE%E5%9E%8B%E6%B6%88%E5%8E%BB%E3%81%AB%E3%81%A4%E3%81%84%E3%81%A6/)
* [概要 | Scala Documentation](https://docs.scala-lang.org/ja/overviews/reflection/overview.html)
* [型タグとマニフェスト | Scala Documentation](https://docs.scala-lang.org/ja/overviews/reflection/typetags-manifests.html)
