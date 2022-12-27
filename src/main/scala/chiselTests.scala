import chisel3._
// import chisel3.util._
import chisel3.ExplicitCompileOptions.Strict
import chisel3.internal.sourceinfo.UnlocatableSourceInfo
import depTypes.*

object chiselTests {
  abstract class mBundle(implicit compileOptions: CompileOptions) extends Bundle()(using compileOptions) {
    override protected def _usingPlugin: Boolean = true
  }
  class Blinky(freq: Int, startOn: Boolean = false) extends Module()(using Strict) { // null.asInstanceOf[CompileOptions]
    val io = IO(new mBundle { // err in
      // https://github.com/chipsalliance/chisel3/blob/7372c9e2eed082d35abbe55f856d03fda68dc0be/core/src/main/scala/chisel3/Aggregate.scala#L1218
      //   val led0 = Output(Bool.apply())
    })
  }

  def callWire[I <: Int]() = {
    // val wi = 1
    val wiretype1 = (new UIntFactory {}).apply(8.W)
    println(("wiretype1", wiretype1))
    val wi = Wire(wiretype1)(UnlocatableSourceInfo, Strict)
    /* chiselException: Error: Not in a UserModule. Likely cause: Missed Module() wrap */
    wireTp[I, UInt](wi) // wireTp[Nothing, Int]
  }

  def run = {
//   https://github.com/chipsalliance/chisel3/blob/7372c9e2eed082d35abbe55f856d03fda68dc0be/core/src/main/scala/chisel3/Aggregate.scala#L1292
    val w1 = callWire() // wireTp[Nothing, UInt]
    println(w1)
    // val newValue = (new chisel3.stage.ChiselStage).emitFirrtl(new Blinky(1000))
    // println(newValue)

// https://docs.scala-lang.org/scala3/guides/migration/incompat-other-changes.html#non-private-constructor-in-private-class

  }
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
  } */
}
