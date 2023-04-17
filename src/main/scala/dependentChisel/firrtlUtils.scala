package dependentChisel

import com.doofin.stdScalaJvm.*
import com.doofin.stdScala.mainRunnable

import firrtl.stage.FirrtlMain
import java.io.*
import scala.util.Try
import scala.util.Failure
import scala.util.Success

object firrtlUtils extends mainRunnable {

  override def main(args: Array[String]): Unit = {}

  /** convert firrtl to verilog */
  def firrtl2verilog(firrtlStr: String): Try[String] = {
    Try {
      val fn = newVirtualFile(firrtlStr, ".fir")
      val fout = newVirtualFile("", ".v")
      FirrtlMain.main(s"-i $fn -o $fout -X verilog".split(" "))
      readTextFile(fout)
    } match
      case x @ Failure(exception) =>
        println(
          "compile firrtl to verilog Failure: \n" + exception.getMessage()
        )
        x
      case x @ Success(value) =>
        println("compile firrtl to verilog Succeed")
        x

  }

  /** create a VirtualFile with content and suffix,returns its path */
  def newVirtualFile(content: String, suffix: String): String = {
    val vFile = File.createTempFile("temp1", suffix)
    val vFilePath = vFile.getPath()

    // Write to temp file
    val out = new BufferedWriter(new FileWriter(vFile)) {
      write(content)
    }

    out.close()

    // pp(vFilePath)
    // pp(readTextFile(tempFilePat))
    vFile.deleteOnExit() // Delete temp file when program exits.
    vFilePath
  }

}
