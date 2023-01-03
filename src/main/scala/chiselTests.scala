import chisel3._
// import chisel3.util._

import chisel3.ExplicitCompileOptions.Strict
import chisel3.internal.sourceinfo.UnlocatableSourceInfo
import depTypes.*
import chisel3.stage.ChiselGeneratorAnnotation
import firrtl.AnnotationSeq

object chiselTests {

  def run = {
// req compiler plugin:  https://github.com/chipsalliance/chisel3/blob/7372c9e2eed082d35abbe55f856d03fda68dc0be/core/src/main/scala/chisel3/Aggregate.scala#L1292

    val mod1 = new EmptyMod1()
    val mod2 = new Blinky2()

    // bug ! requirement failed: must be inside Builder context
    val fir = (new chisel3.stage.ChiselStage).emitVerilog({ new EmptyMod1() })

    println(fir)
    // val w1 = callWire() // wireTp[Nothing, UInt]
    // println(w1)

// https://docs.scala-lang.org/scala3/guides/migration/incompat-other-changes.html#non-private-constructor-in-private-class

  }

  class mBundle extends Bundle()(using Strict) {
    // for chisel 3.5.5,try to bypass _usingPlugin check which is filled by scala 2 macros
    override protected def _usingPlugin: Boolean = true
  }

  class mModule extends Module()(using Strict)

  class EmptyMod1() extends mModule {} // null.asInstanceOf[CompileOptions]

  class Blinky() extends mModule { // null.asInstanceOf[CompileOptions]
    val io = IO(new mBundle { // err in
      // https://github.com/chipsalliance/chisel3/blob/7372c9e2eed082d35abbe55f856d03fda68dc0be/core/src/main/scala/chisel3/Aggregate.scala#L1218
      //   val led0 = Output(Bool.apply())
    })
  }

  class Blinky2() extends mModule { // null.asInstanceOf[CompileOptions]
    val io = IO(new Bundle { // err in
      // https://github.com/chipsalliance/chisel3/blob/7372c9e2eed082d35abbe55f856d03fda68dc0be/core/src/main/scala/chisel3/Aggregate.scala#L1218
      val led0 = Output((new BoolFactory {}).apply())
    })
  }
  def callWire[I <: Int]() = {
    // val wi = 1
    val wiretype1 = (new UIntFactory {}).apply(8.W)
    println(("wiretype1", wiretype1))
    val wi = new mModule { Wire(wiretype1)(UnlocatableSourceInfo, Strict) }
    /* chiselException: Error: Not in a UserModule. Likely cause: Missed Module() wrap
    https://github.com/chipsalliance/chisel3/blob/1654d87a02ca799bf12805a611a91e7524d49843/core/src/main/scala/chisel3/internal/Builder.scala#L667
     */
    /*requirement failed: must be inside Builder context
     https://github.com/chipsalliance/chisel3/blob/1654d87a02ca799bf12805a611a91e7524d49843/core/src/main/scala/chisel3/internal/Builder.scala#L483
     */
    wireTp[I, mModule](wi) // wireTp[Nothing, Int]

  }

  /*
  try just use ADT. case class,etc
  https://github.com/chipsalliance/firrtl/wiki/Understanding-Firrtl-Intermediate-Representation
   */
  case class Mod()
}

/* (new chisel3.stage.ChiselStage).execute(
      Array("-X", "verilog"),
      AnnotationSeq(Seq(ChiselGeneratorAnnotation(() => new EmptyMod1())))
    ) */
/* class Blinky(freq: Int, startOn: Boolean = false) extends Module {
    val io = IO(new Bundle {
      val led0 = Output(Bool())
    })
    // Blink LED every second using Chisel built-in util.Counter
    val led = RegInit(startOn.B)
    val (_, counterWrap) = Counter(true.B, freq / 2)
    when(counterWrap) {
      led := ~led
    }
    io.led0 := led
  }

  val cfg = Array(
      "--emission-options=disableMemRandomization,disableRegisterRandomization"
    )
 */
