package rv32isc

import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec

import java.io.PrintWriter
import java.io.File

import config.Configs._

trait MemInstTestFunc {
    // 生成MEM_INST_SIZE条随机指令进行测试
    val inst_list =
        Seq.fill(MEM_INST_SIZE)(scala.util.Random.nextInt().toLong & 0x00ffffffffL)

    // 为随机指令生成hex文本文件
    def genMemInstHex(): Unit = {
        val memFile = new File(
          System.getProperty("user.dir") + "/src/test/scala/rv32isc/randMemInst.hex"
        )
        memFile.createNewFile()
        val memPrintWriter = new PrintWriter(memFile)
        for (i <- 0 to MEM_INST_SIZE - 1) {
            memPrintWriter.println(inst_list(i).toHexString)
        }
        memPrintWriter.close()
    }

    def testFn(dut: MemInst): Unit = {
        // 依次读取所有的指令，与inst_list进行匹配
        for (i <- 0 to MEM_INST_SIZE - 1) {
            dut.io.addr.poke((i * INST_BYTE_WIDTH).U)   // 作为地址，应该左移两位，即乘以4
            dut.io.inst.expect(inst_list(i).U)
        }
    }
}

class MemInstTest extends AnyFlatSpec with ChiselScalatestTester with MemInstTestFunc {
    "MemInst" should "pass" in {
        // 先生成随机hex文件，再进行测试
        genMemInstHex()
        test(new MemInst(true)) { dut =>
            testFn(dut)
        } 
    }
}