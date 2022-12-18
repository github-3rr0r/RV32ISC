package rv32isc

import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec

import config.Configs._

trait PCRegTestFunc {
    // 生成十个随机地址用于测试
    val target_list =
        Seq.fill(10)(scala.util.Random.nextInt().toLong & 0x00ffffffffL)

    def testFn(dut: PCReg): Unit = {
        // 初始化状态
        dut.io.ctrlBranch.poke(false.B)
        dut.io.ctrlJump.poke(false.B)
        dut.io.resultBranch.poke(false.B)
        dut.io.addrTarget.poke(START_ADDR)
        dut.io.addrOut.expect(START_ADDR)
        
        var addr: Long = START_ADDR

        // 正常自增功能
        for (target <- target_list) {
            dut.io.addrTarget.poke(target.U)
            addr += ADDR_BYTE_WIDTH
            dut.clock.step()
            dut.io.addrOut.expect(addr.U)
        }

        // 跳转功能测试
        dut.io.ctrlJump.poke(true.B)
        for (target <- target_list) {
            dut.io.addrTarget.poke(target.U)
            dut.clock.step()
            dut.io.addrOut.expect(target.U)
            addr = target
        }
        dut.io.ctrlJump.poke(false.B)

        // 分支功能测试
        // 分支指令，但分支不成功
        dut.io.ctrlBranch.poke(true.B)
        for (target <- target_list) {
            dut.io.addrTarget.poke(target.U)
            addr += ADDR_BYTE_WIDTH
            dut.clock.step()
            dut.io.addrOut.expect(addr.U)
        }

        // 分支指令，且分支成功
        dut.io.resultBranch.poke(true.B)
        for (target <- target_list) {
            dut.io.addrTarget.poke(target.U)
            addr += ADDR_BYTE_WIDTH
            dut.clock.step()
            dut.io.addrOut.expect(target.U)
            addr = target
        }
    }
}

class PCRegTest extends AnyFlatSpec with ChiselScalatestTester with PCRegTestFunc {
    "PCReg" should "pass" in {
        test(new PCReg) { dut =>
            testFn(dut)
        } 
    }
}