package dependentChisel

import chisel3.*
// import chisel3.util._

import chisel3.ExplicitCompileOptions.Strict
import chisel3.internal.sourceinfo.UnlocatableSourceInfo
import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.internal.sourceinfo.SourceInfo
import firrtl.AnnotationSeq

import depTypes.*
import com.doofin.stdScalaJvm.*
import com.doofin.stdScala.mainRunnable
/* what info   does chisel macro and plugin provide?
 */
object chiselBlinky extends mainRunnable {
  implicit val srcIfo: SourceInfo = UnlocatableSourceInfo
  implicit val compOpt: CompileOptions = Strict

  override def main(args: Array[String]): Unit = {
// req compiler plugin:  https://github.com/chipsalliance/chisel3/blob/7372c9e2eed082d35abbe55f856d03fda68dc0be/core/src/main/scala/chisel3/Aggregate.scala#L1292

    val mod1 = new EmptyMod1()
    // val mod2 = new Blinky2()

    // bug ! requirement failed: must be inside Builder context
    val fir = (new chisel3.stage.ChiselStage).emitVerilog({ new EmptyMod1() })

    println(fir)
    // val w1 = callWire() // wireTp[Nothing, UInt]
    // println(w1)

// https://docs.scala-lang.org/scala3/guides/migration/incompat-other-changes.html#non-private-constructor-in-private-class

  }

  class bd1 extends Bundle { // err in
    // https://github.com/chipsalliance/chisel3/blob/7372c9e2eed082d35abbe55f856d03fda68dc0be/core/src/main/scala/chisel3/Aggregate.scala#L1218
    val led0 = Output((new BoolFactory {}).apply())
  }
// example from https://github.com/chipsalliance/chisel3#led-blink
  case class Blinky2(freq: Int, startOn: Boolean = false) extends mModule { // null.asInstanceOf[CompileOptions]
    import chisel3.util.*
    val io = IO(new bd1)
    val led = RegInit(startOn.B)
    val (_, counterWrap) = Counter(true.B, freq / 2)
    when(counterWrap) {
      // led := ~led //this uses macros which doesn't work
      led := led.do_unary_~
    }
    io.led0 := led
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

  class cBundle extends Bundle()(using Strict) {
    // for chisel 3.5.5,try to bypass _usingPlugin check which is filled by scala 2 macros
    override protected def _usingPlugin: Boolean = true
  }

  class mModule extends Module()(using Strict)

  class EmptyMod1() extends mModule {} // null.asInstanceOf[CompileOptions]

  /*
  try just use ADT. case class,etc
  https://github.com/chipsalliance/firrtl/wiki/Understanding-Firrtl-Intermediate-Representation
   */
  case class Mod()

  /*   case class GCD() extends Module {
    implicit val srcIfo: SourceInfo = UnlocatableSourceInfo
    val io = IO(new Bundle {
      val a = Input(UInt(32.W))
      val b = Input(UInt(32.W))
      val e = Input(Bool())
      val z = Output(UInt(32.W))
      val v = Output(Bool())
    })
    val x = Reg(UInt(32.W))
    val y = Reg(UInt(32.W))

//  use a override method?
    when(x > y) {
      x := x -% y
    }
      .otherwise {
        y := y -% x
      }
    when(io.e) {
      x := io.a;
      y := io.b
    }
    io.z := x
    io.v := y === 0.U
  } */

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
