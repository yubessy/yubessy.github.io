<!-- $theme: gaia -->

### 不動点コンビネータ

##### @yubessy

#### 0x64物語 Reboot #12

#### "λ"

---

### Q1. 階乗を計算せよ

---

### A1

---

### Q2. ループを使わずに階乗を計算せよ

---

### A2

---

### Q3. ループと再帰を使わずに階乗を計算せよ

---

### A3

```
(fun g x -> (fun h y -> g (h h) y) (fun h y -> g (h h) y) x)(fun f n -> if n == 0 then 1 else n * f(n - 1))(5)
```

---

### なぜこうなるのか

最後の式は

`fun f n -> if n == 0 then 1 else n * f(n - 1)`

階乗の関数定義とよく似ている

`fact = fun n -> if n == 0 then 1 else n * fact(n - 1)`

違いは関数定義ではなく **ラムダ式** であること

---

### なぜこうなるのか

ここでもし以下の式をみたすラムダ式 `Z` があれば・・・

`Z g x = g (Z g) x`

```
  Z (fun f n -> if n == 0 then 1 else n * f(n - 1)) 3
= (fun f n -> if n == 0 then 1 else n * f(n - 1)) (Z (fun f n -> if n == 0 then 1 else n * f(n - 1))) 3
= (fun n -> if n == 0 then 1 else n * (Z (fun f n -> if n == 0 then 1 else n * f(n - 1)))(n - 1)) 3
= if 3 == 0 then 1 else 3 * (Z (fun f n -> if n == 0 then 1 else n * f(n - 1)))(3 - 1)
= 3 * (Z (fun f n -> if n == 0 then 1 else n * f(n - 1)))(2)
= ...
= 3 * 2 * 1 * 1
```

---

### なぜこうなるのか

前半の式はこの `Z` を満たす

`fun g x -> (fun h y -> g (h h) y) (fun h y -> g (h h) y) x`

```
  Z g x
= (fun g x -> (fun h y -> g (h h) y) (fun h y -> g (h h) y) x) g x
= (fun h y -> g (h h) y) (fun h y -> g (h h) y) x
= g ((fun h y -> g (h h) y) (fun h y -> g (h h) y)) x
= g ((fun h x -> g (h h) x) (fun h y -> g (h h) y)) x
= g ((fun x -> g ((fun h y -> g (h h) y) (fun h y -> g (h h) y)) x)) x
= g (Z g) x
```

---

### なぜこうなるのか

よって以下の式は5の階乗を計算する

```
(fun g x -> (fun h y -> g (h h) y) (fun h y -> g (h h) y) x)(fun f n -> if n == 0 then 1 else n * f(n - 1))(5)
```

このように無名関数のみで再帰に相当する計算を行うことを **無名再帰** という
またZのような性質をもつ関数を **不動点コンビネータ** という

---

### ところで

---

### Zはどんな方を持つか？

