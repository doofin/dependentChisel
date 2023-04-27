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
      // new FifoRegister(1) // ok
      // new BubbleFifo(1, 2)
      new BubbleFifo(4, 6)
      // new FifoRegister(5) // ok
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
    /** Input */
    val write = newIO[1](VarType.Input) // Bool() = UInt<1>
    /** Output */
    val full = newIO[1](VarType.Output)

    /** Input */
    val din = newIODym(size, VarType.Input)
  }

  class ReaderIO(size: Int)(using mli: ModLocalInfo) {

    /** Input */
    val read = newIO[1](VarType.Input) // Bool() = UInt<1>
    /** Output */
    val empty = newIO[1](VarType.Output) // Bool() = UInt<1>
    /** Output */
    val dout = newIODym(size, VarType.Output)
  }

  class FifoRegister(using parent: GlobalInfo)(size: Int) extends UserModule {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)

    val (empty, full) = (newLit(0), newLit(1))

    val stateReg = newRegInitDym(empty)
    val dataReg = newRegInitDym(newLit(0, Some(size))) // TODO

    If(stateReg === empty) {
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
    }
    /* original:
  io.enq.full := (stateReg === full)
  io.deq.empty := (stateReg === empty)
  io.deq.dout := dataReg */
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

  /** This is a bubble FIFO.
    */
  class BubbleFifo(using parent: GlobalInfo)(size: Int, depth: Int) extends UserModule {
    /*   val io = IO(new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  })
     */
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)

    val buffers = Array.fill(depth) { newMod(new FifoRegister(size)) }

    val depList = 0 until depth - 1
    assert(depList.nonEmpty)
    depList foreach { i =>
      buffers(i + 1).enq.din := buffers(i).deq.dout
      buffers(i + 1).enq.write := buffers(i).deq.empty
      buffers(i).deq.read := buffers(i + 1).enq.full
    }

    // bulk conn : io.enq <> buffers(0).io.enq
    buffers(0).enq.din := enq.din // not work! input can't be lhs,but flip lhs rhs works?
    enq.full := buffers(0).enq.full

    // enq.write (enq : { flip write ) = Expression io.io_i_3 is used as a SinkFlow but can only be used as a SourceFlow
    buffers(0).enq.write := enq.write // not work! input can't be lhs

    // bulk conn :  io.deq <> buffers(depth - 1).io.deq
    deq.dout := buffers(depth - 1).deq.dout
    deq.empty := buffers(depth - 1).deq.empty
    buffers(depth - 1).deq.read := deq.read // need to flip lhs,rhs
  }

}
/* bulk conn
  FifoRegister.io.enq.din <= io.enq.din @[bubbleFIFO.scala 153:10]
    io.enq.full <= FifoRegister.io.enq.full @[bubbleFIFO.scala 153:10]
    FifoRegister.io.enq.write <= io.enq.write @[bubbleFIFO.scala 153:10]

    io.deq.dout <= FifoRegister_1.io.deq.dout @[bubbleFIFO.scala 154:10]
    io.deq.empty <= FifoRegister_1.io.deq.empty @[bubbleFIFO.scala 154:10]
    FifoRegister_1.io.deq.read <= io.deq.read @[bubbleFIFO.scala 154:10]
 */

/*
class BubbleFifo(size: Int, depth: Int) extends Module {
  val io = IO(new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  })

  val buffers = Array.fill(depth) { Module(new FifoRegister(size)) }
  for (i <- 0 until depth - 1) {
    buffers(i + 1).io.enq.din := buffers(i).io.deq.dout
    buffers(i + 1).io.enq.write := ~buffers(i).io.deq.empty
    buffers(i).io.deq.read := ~buffers(i + 1).io.enq.full
  }
  io.enq <> buffers(0).io.enq
  io.deq <> buffers(depth - 1).io.deq
}
 */
