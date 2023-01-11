import firrtl.*
import firrtl.ir.*
import firrtl.passes._
import firrtl.transforms._
import depTypes.*
import com.doofin.stdScalaJvm.*
import com.doofin.stdScala.mainRunnable
// import pprint.*
/* directly work on firrtl instead of chisel layer
  try just use ADT. case class,etc
  https://github.com/chipsalliance/firrtl/wiki/Understanding-Firrtl-Intermediate-Representation
  https://github.com/chipsalliance/firrtl/wiki/Cookbook
 */
object firrtlTests extends mainRunnable {

  override def main(args: Array[String]): Unit = Circuit1

  def run = {
    Module(NoInfo, "Adder", Seq(), Block(Seq()))
    // Circuit(NoInfo, Seq(Module(NoInfo, Seq(Port(NoInfo,null,null )), "Adder")
  }

  def Circuit1 = {

    val input = """
circuit tag_array_ext :
  module tag_array_ext :
    input RW0_clk : Clock
    input RW0_addr : UInt<6>
    input RW0_wdata : UInt<80>
    output RW0_rdata : UInt<80>
    input RW0_en : UInt<1>
    input RW0_wmode : UInt<1>
    input RW0_wmask : UInt<4>
  
    inst mem_0_0 of rawr
    inst mem_0_1 of rawr
    inst mem_0_2 of rawr
    inst mem_0_3 of rawr
    mem_0_0.clk <= RW0_clk
    mem_0_0.addr <= RW0_addr
    node RW0_rdata_0_0 = bits(mem_0_0.dout, 19, 0)
    mem_0_0.din <= bits(RW0_wdata, 19, 0)
    mem_0_0.write_en <= and(and(RW0_wmode, bits(RW0_wmask, 0, 0)), UInt<1>("h1"))
    mem_0_1.clk <= RW0_clk
    mem_0_1.addr <= RW0_addr
    node RW0_rdata_0_1 = bits(mem_0_1.dout, 19, 0)
    mem_0_1.din <= bits(RW0_wdata, 39, 20)
    mem_0_1.write_en <= and(and(RW0_wmode, bits(RW0_wmask, 1, 1)), UInt<1>("h1"))
    mem_0_2.clk <= RW0_clk
    mem_0_2.addr <= RW0_addr
    node RW0_rdata_0_2 = bits(mem_0_2.dout, 19, 0)
    mem_0_2.din <= bits(RW0_wdata, 59, 40)
    mem_0_2.write_en <= and(and(RW0_wmode, bits(RW0_wmask, 2, 2)), UInt<1>("h1"))
    mem_0_3.clk <= RW0_clk
    mem_0_3.addr <= RW0_addr
    node RW0_rdata_0_3 = bits(mem_0_3.dout, 19, 0)
    mem_0_3.din <= bits(RW0_wdata, 79, 60)
    mem_0_3.write_en <= and(and(RW0_wmode, bits(RW0_wmask, 3, 3)), UInt<1>("h1"))
    node RW0_rdata_0 = cat(RW0_rdata_0_3, cat(RW0_rdata_0_2, cat(RW0_rdata_0_1, RW0_rdata_0_0)))
    RW0_rdata <= mux(UInt<1>("h1"), RW0_rdata_0, UInt<1>("h0"))

  extmodule rawr :
    input clk : Clock
    input addr : UInt<6>
    input din : UInt<32>
    output dout : UInt<32>
    input write_en : UInt<1>
  
    defname = rawr
"""

// Parse the input
    val parsed: Circuit = firrtl.Parser.parse(input)
    pp(parsed)
    val state = CircuitState(parsed, UnknownForm)

// Designate a series of transforms to be run in this order
    val transforms: Seq[Transform] =
      Seq(ToWorkingIR, ResolveKinds, InferTypes, new InferWidths)

// Run transforms and capture final state
    val finalState = transforms.foldLeft(state) { (c: CircuitState, t: Transform) =>
      t.runTransform(c)
    }

// Emit output
    println(finalState.circuit.serialize)
  }
}
