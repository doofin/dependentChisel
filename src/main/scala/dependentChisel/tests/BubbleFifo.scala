package dependentChisel.tests

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.typesAndSyntax.varDecls.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import com.doofin.stdScala.mainRunnable
import dependentChisel.codegen.compiler.*
import dependentChisel.firrtlUtils
import dependentChisel.typesAndSyntax.varDecls.*

/*
 https://github.com/schoeberl/chisel-examples/blob/master/src/main/scala/simple/BubbleFifo.scala
 */
object BubbleFifo extends mainRunnable {
  override def main(args: Array[String] = Array()): Unit = run

  def run = {
    // (1, 2, 3).mapConst((x: Int) => x * 2)
    val (mod, globalCircuit) = makeModule { implicit p =>
//   new IfElse1
      new FifoRegisterSimp1(1) // ok
    }

    val fMod = chiselMod2firrtlCircuits(mod)
    val firCirc = firrtlCircuits2str(fMod)
    println(firCirc)
    firrtlUtils.firrtl2verilog(firCirc)

  }

  /* TODO parameterised IO:  doesn't support parameterised IO yet.consider :
  1.allow newInput be called outside module at arbitary places,only add it to IO in invoke site
  2.mimic chisel : discriminate between bundle and module like chisel do */

  /*
  enq : { flip write : UInt<1>, full : UInt<1>, flip din : UInt<1>}

  class WriterIO(size: Int) extends Bundle {
  val write = Input(Bool())
  val full = Output(Bool())
  val din = Input(UInt(size.W))
} */

  class WriterIO(size: Int)(using mli: ModLocalInfo) {
    // newIO[2](VarType.Input)
    val write = newIO[1](VarType.Input) // Bool() = UInt<1>
    val full = newIO[1](VarType.Output)
    val din = newIODym(size, VarType.Input)
  }
  /* class ReaderIO(size: Int) extends Bundle {
  val read = Input(Bool())
  val empty = Output(Bool())
  val dout = Output(UInt(size.W))
}
   */
  class ReaderIO(size: Int)(using mli: ModLocalInfo) {
    val read = newIO[1](VarType.Input) // Bool() = UInt<1>
    val empty = newIO[1](VarType.Output) // Bool() = UInt<1>
    val dout = newIODym(size, VarType.Output)
  }

  class FifoRegister(using parent: GlobalInfo)(size: Int) extends UserModule {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size) // TODO fix create multiple inst

    // val (empty, full) = (Lit[0](0), Lit[1](1))
    val (empty, full) = (newLit(0), newLit(1)) // TODO should be 0,1

    val stateReg = newRegInitDym(empty)
    val dataReg = newRegInitDym(newLit(size))
    // stateReg := empty

    /* when(stateReg === empty) {
      when(io.enq.write) {
        stateReg := full
        dataReg := io.enq.din
      }
    }.elsewhen(stateReg === full) {
      when(io.deq.read) {
        stateReg := empty
        dataReg := 0.U // just to better see empty slots in the waveform
      }
    } */

    IfElse(stateReg === empty) {
      stateReg := full
    } {
      stateReg := empty
    }

    /* IfElse(stateReg === empty.asUnTyped) {
      IfElse(enq.write) {
        stateReg := full
        dataReg := enq.din
      } {
        If(stateReg === full) {
          If(deq.read) {
            stateReg := empty
            dataReg := newLit(0)
          }
        }
      }
      enq.full := stateReg.asTyped[1]
    } { enq.full := stateReg.asTyped[1] } */
    enq.full := (stateReg === full)
    deq.empty := (stateReg === empty)
    deq.dout := dataReg
  }

  class FifoRegisterSimp1(using parent: GlobalInfo)(size: Int) extends UserModule {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size) // TODO fix create multiple inst

    // val (empty, full) = (Lit[0](0), Lit[1](1))
    val (empty, full) = (newLit(0), newLit(1)) // TODO should be 0,1

    val stateReg = newRegInitDym(empty)
    val dataReg = newRegInitDym(newLit(size))
    IfElse(stateReg === empty) {
      stateReg := full
    } {
      stateReg := empty
    }

    enq.full := (stateReg === full)
    deq.empty := (stateReg === empty)
    deq.dout := dataReg
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
      dataReg := io.\enq.din
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
