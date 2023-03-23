package dependentChisel

import com.doofin.stdScalaJvm.*
import com.doofin.stdScala.mainRunnable

import firrtl.stage.FirrtlMain
import java.io.*

object firrtlUtils extends mainRunnable {

  override def main(args: Array[String]): Unit = {}

  /** convert firrtl to verilog */
  def firrtl2verilog(firrtlStr: String) = {
    val fn = newVirtualFile(firrtlStr, ".fir")
    val fout = newVirtualFile("", ".v")
    FirrtlMain.main(s"-i $fn -o $fout -X verilog".split(" "))
    readTextFile(fout)
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
