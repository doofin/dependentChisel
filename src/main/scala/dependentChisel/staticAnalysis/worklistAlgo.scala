package dependentChisel.staticAnalysis
import com.doofin.stdScalaJvm.*

import scala.collection.{immutable, mutable}

import MonotoneFramework.*
import chisel3.util.is

object worklistAlgo {
  trait Worklist[t] {
    def extract: t
    def insert(e: t): Unit
    def insertAll(e: Seq[t]): Unit
    def isEmpty: Boolean
//    def empty: Worklist[t]
  }

  class WlStack[t]() extends Worklist[t] {
    val as: mutable.ArrayStack[t] = mutable.ArrayStack()

    override def extract: t = as.pop()

    override def insert(e: t): Unit = as += e

    override def insertAll(e: Seq[t]): Unit = as ++= e

    override def isEmpty: Boolean = as.isEmpty
  }

  /** worklist algorithm on program graph
    *
    * @param progGraph_
    * @param transferF
    * @param smallerThan
    * @param lubOp
    * @param initD
    *   for program point 0 if it's forward analysis, or exiting point if backward analysis
    * @param bottomD
    *   for other program points
    * @param entryExitPoint
    *   the (entry, exit) program points. forward analysis only use entry point, backward analysis
    *   only use exit
    * @param isForward
    *   true for forward analysis,false for backward analysis
    * @return
    */
  def wlAlgoProgGraphP[domainT, stmtT](
      progGraph_ : List[(Int, stmtT, Int)],
      transferF: ((Int, stmtT, Int), domainT) => domainT,
      smallerThan: (domainT, domainT) => Boolean,
      lubOp: (domainT, domainT) => domainT,
      initD: domainT,
      bottomD: domainT,
      entryExitPoint: (Int, Int),
      isForward: Boolean = true
  ): Map[Int, domainT] = {

    val mutList: Worklist[Int] = new WlStack()

    //  for backward analysis just reverse the edges and swap entry exit (or init final)
    val progGraph =
      if isForward then progGraph_
      else progGraph_.map { case (a, st, c) => (c, st, a) }

    val (entryPoint, exitPoint) =
      if isForward then entryExitPoint else (entryExitPoint._2, entryExitPoint._1)

    // pp(progGraph)
    //    get program points
    val progPoints = progGraph.flatMap(x => List(x._1, x._3)).distinct

    mutList.insertAll(progPoints)

    val resMapMut: mutable.Map[Int, domainT] = mutable.Map()

//    initialize at each program points,set to init for point 0 (first loop)
    progPoints foreach { q =>
      resMapMut(q) = if (q == 0) then initD else bottomD
    }

    // keep applying transferF to program graph until the node value is stable
    // according to the ascending order property, this will terminate
    var steps = 0
    while (!mutList.isEmpty) {
      steps += 1
      //      pp(wlMut.toList, "work list : ")

      val q_wl = mutList.extract

      val progGraphTups =
        progGraph.filter(_._1 == q_wl) // prog point tuples after q_wl

      progGraphTups foreach { case tup @ (pre, stmtT, post) =>
        val preMap = resMapMut(pre)
        val postMap = resMapMut(post) // AA(q dot)
        val preMapTransfered = transferF(tup, preMap)

//          update if preMapAnalysised >=  postMap (not <=)
//        println("doUpdate:", subOrderOp, preMapTransfered, postMap) // subOrderOp can be null
        val doUpdate = !smallerThan(preMapTransfered, postMap)

//        println(s"doUpdate if presetF ${pre} notSubOrder postset ${post}:: at ${post}", doUpdate)
//          pp(preMapTransfered, s"preSetF f(${pre}):")
//          pp(postMap, s"postSet ${post}:")

        if (doUpdate) {

          val lubR = lubOp(postMap, preMapTransfered)
//            pp(lubR, "lub : ")
          resMapMut(post) = lubR
//            wlMut += post
          mutList.insert(post)
        } else {
//            println(s"no Update for ${edge} ")
        }
      }
    }
    println(s"iter step : ${steps}")
    resMapMut.toMap
  }
}
