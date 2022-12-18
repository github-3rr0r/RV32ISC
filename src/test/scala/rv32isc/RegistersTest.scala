package rv32isc

import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec

import config.Configs._

trait RegistersTestFunc {
    // 随机填入数据
    val oprand_list = Seq.fill(REG_NUMS)(scala.util.Random.nextInt().toLong & 0x00ffffffffL)

    def testRegs(dut: Registers): Unit = {
        // 初始化状态
        for (i <- 0 to REG_NUMS - 1) {
            dut.io.bundleReg.rs1.poke(i.U)
            dut.io.dataRead1.expect(0.U)
            dut.io.bundleReg.rs2.poke(i.U)
            dut.io.dataRead2.expect(0.U)
        }
        // 写入
        for (i <- 0 to REG_NUMS - 1) {
            dut.io.ctrlRegWrite.poke(true.B)
            dut.io.bundleReg.rd.poke(i.U)
            dut.io.dataWrite.poke(oprand_list(i))
            dut.clock.step()
        }
        // 读取
        for (i <- 0 to REG_NUMS - 1) {
            dut.io.bundleReg.rs1.poke(i.U)
            if (i == 0) {
                dut.io.dataRead1.expect(0.U)
            } else {
                dut.io.dataRead1.expect(oprand_list(i))
            }
            
            dut.io.bundleReg.rs2.poke(i.U)
            if (i == 0) {
                dut.io.dataRead2.expect(0.U)
            } else {
                dut.io.dataRead2.expect(oprand_list(i))
            }
        }
        // 不能写时尝试写0
        for (i <- 0 to REG_NUMS - 1) {
            dut.io.ctrlRegWrite.poke(false.B)
            dut.io.bundleReg.rd.poke(i.U)
            dut.io.dataWrite.poke(0)
            dut.clock.step()
        }
        // 再次读
        for (i <- 0 to REG_NUMS - 1) {
            dut.io.bundleReg.rs1.poke(i.U)
            if (i == 0) {
                dut.io.dataRead1.expect(0.U)
            } else {
                dut.io.dataRead1.expect(oprand_list(i))
            }
            
            dut.io.bundleReg.rs2.poke(i.U)
            if (i == 0) {
                dut.io.dataRead2.expect(0.U)
            } else {
                dut.io.dataRead2.expect(oprand_list(i))
            }
        }
    }
}

class RegistersTest extends AnyFlatSpec with ChiselScalatestTester with RegistersTestFunc {
    "Registers" should "pass" in {
        test(new Registers) { dut =>
            testRegs(dut)
        }
    }
}