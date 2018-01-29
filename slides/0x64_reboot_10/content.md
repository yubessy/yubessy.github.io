<!-- $theme: gaia -->

### Scala Native

##### @yubessy

#### 0x64物語 Reboot #10

#### "コンパイラ"

---

### なぜコンパイラ回で？

* 「プログラミング言語」を構成するもの
  * 定義: 構文・意味論
  * 実装: コンパイラ・インタプリタ
* １言語１コンパイラ(・インタプリタ)時代の終焉
  * 目的に応じて x 言語 -> o **実行手段** を選ぶ
  * そこで Scala Native ですよ

---

### Scala Native

* Scalaのバイトコードコンパイラ
* 本来のScalaコンパイラ
  * JVM言語 -> Javaがないと動かせない
* Scala Native
  * バイトコードを出力 -> **Java無しで動く**

---

### 特長

* LLVMを利用
  * 最適化機構を利用できる
  * 多くのプラットフォームに対応
* セルフホスティング(?)
  * Scala Native自体がScalaで実装
* SBTプラグインとして提供
  * クロスビルドしやすい

---

### とりあえず

```shell
$ sbt new scala-native/scala-native.g8
```

```scala
object Main {
  def main(args: Array[String]): Unit =
    println("Hello, world!")
}
```

```shell
$ sbt run
Hello, World!
```

---

### LLVMといえば・・・最適化！

https://github.com/okapies/scala-native-example

* fibonacciを使った簡単な例
  * 再帰
  * 末尾再帰
  * 相互再帰
* ベンチ取ってみる

---

### 再帰 (rec)

```scala
def fib(n: Long): Long = n match {
  case 0 => 0
  case 1 => 1
  case _ => fib(n - 2) + fib(n - 1)
}
```

---

### 末尾再帰 (tail rec)

```scala
def fibImpl(n: Long, a: Long, b: Long): Long = n match {
  case 0 => a
  case _ => fibImpl(n - 1, b, a + b)
}

def fib(n: Long): Long = {
  fibImpl(n, 0, 1)
}
```

---

### 相互再帰 (mut rec)

```scala
def fib(n: Long): Long = n match {
  case 0 => 0
  case _ => fibS(n - 1)
}

def fibS(n: Long): Long = n match {
  case 0 => 1
  case _ => fib(n - 1) + fibS(n - 1)
}
```

---

### ベンチ結果 (μs)

|              |       rec |tail rec|   mut rec |
|--------------|----------:|-------:|----------:|
| JAR          |  74801223 |    262 |  67248872 |
| Native `-O0` | 168336051 |      4 | 164421481 |
| Native `-O2` |  77311107 |      4 |  34026913 |

* Native `-O0` (最適化無し)
  * rec, mute rec はJARより遅い. tail rec は爆速
* Native `-O2` (最適化有り)
  * mute rec がJARを逆転

---

### まとめ

* 今後に期待
