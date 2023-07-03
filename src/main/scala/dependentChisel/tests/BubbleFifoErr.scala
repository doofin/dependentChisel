package dependentChisel.tests

import dependentChisel.typesAndSyntax.chiselModules.*
import dependentChisel.typesAndSyntax.varDecls.*
import dependentChisel.typesAndSyntax.typesAndOps.*
import dependentChisel.typesAndSyntax.statements.*
import dependentChisel.typesAndSyntax.varDecls.*
import dependentChisel.codegen.compiler.*
import dependentChisel.firrtlUtils

/* show case to detect dynamic size mismatch
In line 22
correct : val din = newIODym(size + 1, VarType.Input)
error :   val din = newIODym(size, VarType.Input)
 */
object BubbleFifoErr {
  def main(args: Array[String] = Array()): Unit = run

  def run = {
    val mod = makeModule { implicit p => new FifoRegister(2) }

    chiselMod2verilog(mod)
  }

  class WriterIO(size: Int)(using mli: ModLocalInfo) {

    /** Input */
    val write = newIO[1](VarType.Input)

    /** Output */
    val full = newIO[1](VarType.Output)

    /** Input */
    val din = newIODym(size + 1, VarType.Input) // correct should be just size
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

    enq.full := (stateReg === full)
    deq.empty := (stateReg === empty)
    deq.dout := dataReg
  }

  class BubbleFifo(using parent: GlobalInfo)(size: Int, depth: Int) extends UserModule {
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
    buffers(0).enq.write := enq.write // not work! input can't be lhs

    // bulk conn :  io.deq <> buffers(depth - 1).io.deq
    deq.dout := buffers(depth - 1).deq.dout
    deq.empty := buffers(depth - 1).deq.empty
    buffers(depth - 1).deq.read := deq.read // need to flip lhs,rhs
  }

}

// new FifoRegister(1) // ok
// new BubbleFifo(1, 2) // ok
// new BubbleFifo(2, 3) // ok
