package rv32isc

import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec

import java.io.PrintWriter
import java.io.File

import config.Configs._

trait TopTestFunc {

    def testFn(dut: Top): Unit = {
        dut.clock.setTimeout(0)
        while (dut.io.inst.peekInt() != 0) {
                // println("PC", dut.io.addr.peekInt().toLong.toHexString)
                // println("INST", dut.io.inst.peekInt().toLong.toHexString)
            if (dut.io.addr.peekInt() == 0xb8) {
                println("RES", dut.io.result.peekInt())
                println("RESALU", dut.io.resultALU.peekInt())
                println("RESBRANCH", dut.io.resultBranch.peek())
                println("RESJUMP", dut.io.bundleCtrl.ctrlJump.peek())
                println("SRCCCCC", dut.io.bundleCtrl.ctrlALUSrc.peek())
                println("STORE", dut.io.bundleCtrl.ctrlStore.peek())
                println("LOAD", dut.io.bundleCtrl.ctrlLoad.peek())
                println("RESJAL", dut.io.bundleCtrl.ctrlJAL.peek())
                println("OP:\t", dut.io.bundleCtrl.ctrlOP.peek())
                println("isBranch:\t", dut.io.bundleCtrl.ctrlBranch.peek())
                println("IMM:\t", dut.io.imm.peekInt())
                println("RS1:\t", dut.io.rs1.peekInt())
                println("RS2:\t", dut.io.rs2.peekInt())
                println("PC", dut.io.addr.peekInt().toLong.toHexString)
                println("INST", dut.io.inst.peekInt().toLong.toHexString)
                dut.io.rs2.expect(55.U)
                println("++++++++++++++++++++")
            }
            dut.clock.step(1)
        }
    }
}

class TopTest extends AnyFlatSpec with ChiselScalatestTester with TopTestFunc {
    "Top" should "pass" in {
        test(new Top) { dut =>
            testFn(dut)
        }
    }
}
