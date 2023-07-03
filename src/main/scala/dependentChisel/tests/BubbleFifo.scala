package dependentChisel.tests

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.typesAndSyntax.varDecls.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.codegen.compiler.*

// import dependentChisel.api.*
import dependentChisel.firrtlUtils

import com.doofin.stdScala.mainRunnable

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
/* https://github.com/schoeberl/chisel-examples/blob/master/src/main/scala/simple/BubbleFifo.scala
 */
object BubbleFifo extends mainRunnable {
  override def main(args: Array[String] = Array()): Unit = {
    val mod = makeModule { implicit p =>
      new BubbleFifo(2, 3) // ok
    }
    chiselMod2verilog(mod)
  }

  /*
  enq : { flip write : UInt<1>, full : UInt<1>, flip din : UInt<1>}
  class WriterIO(size: Int) extends Bundle {
  val write = Input(Bool())
  val full = Output(Bool())
  val din = Input(UInt(size.W))
} */

  class WriterIO(using ModLocalInfo)(size: Int :| Positive) {

    /** Input */
    val write = newIO[1](VarType.Input) // Bool is same as UInt<1>
    /** Output */
    val full = newIO[1](VarType.Output)

    /** Input */
    val din = newIODym(size, VarType.Input)
    // val din = newIO(VarType.Input, Some(size))
  }

  class ReaderIO(using ModLocalInfo)(size: Int) {

    /** Input */
    val read = newIO[1](VarType.Input) // Bool() = UInt<1>
    /** Output */
    val empty = newIO[1](VarType.Output) // Bool() = UInt<1>
    /** Output */
    val dout = newIODym(size, VarType.Output)
  }

  class FifoRegister(using GlobalInfo)(size: Int :| Positive) extends UserModule {
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
            /* in the book,it's dataReg := 0.U which has size error,but firrtl allows size
            of rhs>lhs so it won't fail */
            dataReg := newLit(0, Some(size)) // bug? newLit(0)
          }
        }
      }
    }

    enq.full := (stateReg === full)
    deq.empty := (stateReg === empty)
    deq.dout := dataReg
  }

  class BubbleFifo(using GlobalInfo)(size: Int :| Positive, depth: Int)
      extends UserModule {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)

    val buffers = Array.fill(depth) { newMod(new FifoRegister(size)) }

    val depList = 0 until depth - 1
    assert(depList.nonEmpty)
    depList foreach { i =>
      buffers(i + 1).enq.din := buffers(i).deq.dout
      buffers(i + 1).enq.write := ~buffers(i).deq.empty
      buffers(i).deq.read := ~buffers(i + 1).enq.full
    }

    // bulk conn : io.enq <> buffers(0).io.enq
    buffers(0).enq.din := enq.din
    enq.full := buffers(0).enq.full
    buffers(0).enq.write := enq.write

    // bulk conn :  io.deq <> buffers(depth - 1).io.deq
    deq.dout := buffers(depth - 1).deq.dout
    deq.empty := buffers(depth - 1).deq.empty
    buffers(depth - 1).deq.read := deq.read
  }

  def bulkConn(using ModLocalInfo)(enq: WriterIO, enq2: WriterIO) = {
    enq.din := enq2.din
    enq2.full := enq.full
    enq.write := enq2.write

  }
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

// enq.write (enq : { flip write ) = Expression io.io_i_3 is used as a SinkFlow but can only be used as a SourceFlow

/* TODO parameterised IO:  doesn't support parameterised IO yet.consider :
  1.allow newInput be called outside module at arbitary places,only add it to IO in invoke site
  2.mimic chisel : discriminate between bundle and module like chisel do */

// new FifoRegister(1) // ok
// new BubbleFifo(1, 2) // ok
// new FifoRegister(2) // ok
