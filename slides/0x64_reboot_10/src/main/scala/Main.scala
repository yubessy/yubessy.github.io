import scala.collection.mutable

object NaiveFibonacci {
  def fib(n: Long): Long = n match {
    case 0 => 0
    case 1 => 1
    case _ => fib(n - 2) + fib(n - 1)
  }
}

object TailCallFibonacci {
  def fibImpl(n: Long, a: Long, b: Long): Long = n match {
    case 0 => a
    case _ => fibImpl(n - 1, b, a + b)
  }

  def fib(n: Long): Long = {
    fibImpl(n, 0, 1)
  }
}

object MutualTailCallFibonacci {
  def fib(n: Long): Long = n match {
    case 0 => 0
    case _ => fibS(n - 1)
  }

  def fibS(n: Long): Long = n match {
    case 0 => 1
    case _ => fib(n - 1) + fibS(n - 1)
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    val n = args(0).toLong

    print("Naive fibonacci: ")
    val start1 = System.nanoTime()
    println(NaiveFibonacci.fib(n))
    println("Time: " + (System.nanoTime() - start1) + " ns")

    print("Tail call fibonacci: ")
    val start2 = System.nanoTime()
    println(TailCallFibonacci.fib(n))
    println("Time: " + (System.nanoTime() - start2) + " ns")

    print("Mutual tail call fibonacci: ")
    val start3 = System.nanoTime()
    println(MutualTailCallFibonacci.fib(n))
    println("Time: " + (System.nanoTime() - start3) + " ns")
  }
}
