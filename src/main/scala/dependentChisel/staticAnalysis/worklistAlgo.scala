package dependentChisel.staticAnalysis
import com.doofin.stdScalaJvm.*

import scala.collection.{immutable, mutable}

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

  def wlAlgoProgGraphP[domain, stmt](
      mutList: Worklist[Int],
      progGraph: List[(Int, stmt, Int)],
      transferF: ((Int, stmt, Int), domain) => domain,
      smallerThan: (domain, domain) => Boolean,
      lubOp: (domain, domain) => domain,
      initD: domain,
      bottomD: domain,
      isReverse: Boolean = false
  ): Map[Int, domain] = {

    pp(progGraph)

//    get program points from edges,ignore stmt in (Int, Stmt, Int)
    val Q = progGraph.flatMap(x => List(x._1, x._3)).distinct

//    initialise work list
//    val wlMut: mutable.ArrayStack[Int] = mutable.ArrayStack(Q: _*) //flowG: _*
    mutList.insertAll(Q)

    val resMapMut: mutable.Map[Int, domain] = mutable.Map() // 2 -> Set(Var("x"))

//    initialize results at program points,set to init for point 0 (first loop)
    Q foreach { q =>
      resMapMut(q) = if (q == 0) initD else bottomD
    }

//    pp(resMapMut.toMap, "init resMap : ")
    // second loop,keep applying transferF to program graph until the node value is stable
    var steps = 0
    while (!mutList.isEmpty) {
      steps += 1
//      pp(wlMut.toList, "work list : ")

      val q_wl = mutList.extract //        extract

      val progGraphTups =
        progGraph.filter(_._1 == q_wl) // prog point tuples after q_wl

      progGraphTups foreach { case tup @ (pre, _, post) =>
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

/* def wlAlgoMonotone[domain](
      mf: MonotoneFrameworkProgGr[domain, Stmt],
      stmtInput: Stmt,
      varlist: String
  ) = {
    val initMap =
      Map(
        varlist.toCharArray
          .map(x => Var(x.toString) -> mf.init)
          .toList: _*
      )

    val bottomMap =
      Map(
        varlist.toCharArray
          .map(x => Var(x.toString) -> mf.bottomM)
          .toList: _*
      )

    wlAlgoProgGraph[MonotoneFrameworkProgGr.domainMapT[domain]](
      new WlStack[Int](),
      stmtInput,
      mf.transferF,
      mf.smallerThan,
      mf.lub,
      initMap,
      bottomMap
    )
  } */
