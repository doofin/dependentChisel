package dependentChisel.tests

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.typesAndSyntax.varDecls.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import com.doofin.stdScala.mainRunnable
import dependentChisel.codegen.compiler.*
import dependentChisel.firrtlUtils
/*
 https://github.com/schoeberl/chisel-examples/blob/master/src/main/scala/simple/BubbleFifo.scala
 */
object BubbleFifo extends mainRunnable {
  override def main(args: Array[String] = Array()): Unit = run

  def run = {
    // (1, 2, 3).mapConst((x: Int) => x * 2)
    val (mod, globalCircuit) = makeModule { implicit p =>
//   new IfElse1
      new FifoRegisterPre(1) // ok
    }

    val fMod = chiselMod2firrtlCircuits(mod)
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)
    firrtlUtils.firrtl2verilog(firCirc)

  }

  /* TODO parameterised IO:  doesn't support parameterised IO yet.consider :
  1.allow newInput be called outside module at arbitary places,only add it to IO in invoke site
  2.mimic chisel : discriminate between bundle and module like chisel do */

  /* class WriterIO(size: Int) extends Bundle {
  val write = Input(Bool())
  val full = Output(Bool())
  val din = Input(UInt(size.W))
} */

  class WriterIO(size: Int)(using mli: ModLocalInfo) {
    // newIO[2](VarType.Input)
    val write = newIO[1](VarType.Input)
    val full = newIO[1](VarType.Input)
    val din = newIODym(size, VarType.Output)
  }

  class FifoRegisterPre(using parent: GlobalInfo)(size: Int) extends UserModule {
    val enq = new WriterIO(size)

    enq.din := enq.write
  }

  class FifoRegister(using parent: GlobalInfo)(size: Int) extends UserModule {
    val enq = new WriterIO(size)
    // val deq = newMod(new WriterIO(size)) // TODO fix create multiple inst

    // val (empty, full) = (Lit[0](0), Lit[1](1))
    val (empty, full) = (Lit[1](1), Lit[1](1))

    val stateReg = newRegDym(1)
    stateReg := empty

    IfElse(stateReg === empty.asUnTyped) {
      enq.write := stateReg.asTyped[1]
    } { enq.write := stateReg.asTyped[1] }
  }

  /*
class FifoRegister(size: Int) extends Module {
  val io = IO(new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  })

  val empty :: full :: Nil = Enum(2)
  val stateReg = RegInit(empty)
  val dataReg = RegInit(0.U(size.W))

  when(stateReg === empty) {
    when(io.enq.write) {
      stateReg := full
      dataReg := io.enq.din
    }
  }.elsewhen(stateReg === full) {
    when(io.deq.read) {
      stateReg := empty
      dataReg := 0.U // just to better see empty slots in the waveform
    }
  }.otherwise {
    // There should not be an otherwise state
  }

  io.enq.full := (stateReg === full)
  io.deq.empty := (stateReg === empty)
  io.deq.dout := dataReg
} */
}
