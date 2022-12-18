package rv32isc

import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec

import config.Configs._
import utils.OP_TYPES._

trait AluTestFunc {
    // 测试所有功能
    val OP_TYPES_LIST = Seq(
        OP_NOP,
        OP_ADD,
        OP_SUB,
        OP_AND,
        OP_OR,
        OP_XOR,
        OP_SLL,
        OP_SRL,
        OP_SRA,
        // OP_EQ,
        // OP_NEQ,
        // OP_LT,
        // OP_GE
    )

    // 随机的操作数
    val oprand_list =
        Seq.fill(10)(scala.util.Random.nextInt().toLong & 0x00ffffffffL)

    // 用于比对的正确结果
    def alu(a: Long, b: Long, op: UInt, sign: Boolean): (Long, Boolean) = {
        op match {
            case OP_NOP => (0, false)
            case OP_ADD => (a + b, false)
            case OP_SUB => (a - b, false)
            case OP_AND => (a & b, false)
            case OP_OR  => (a | b, false)
            case OP_XOR => (a ^ b, false)
            case OP_SLL => (a << (b & 0x000000001f), false)
            case OP_SRL => (a >>> (b & 0x000000001f), false)
            case OP_SRA => (a.toInt >> (b & 0x000000001f).toInt, false)
            case OP_EQ  => (0, a == b)
            case OP_NEQ => (0, a != b)
            case OP_LT => {
                if (sign) {
                    (0, (a << 32) < (b << 32))
                } else {
                    (0, a < b)
                }
            }
            case OP_GE => {
                if (sign) {
                    (0, (a << 32) >= (b << 32))
                } else {
                    (0, a >= b)
                }
            }
            case _ => (0, false)
        }
    }

    def testOne(dut: Alu, a: Long, b: Long, op: UInt, sign: Boolean): Unit = {
        // 正常测试
        dut.io.bundleAluControl.ctrlBranch.poke(false.B)
        dut.io.bundleAluControl.ctrlALUSrc.poke(false.B)
        dut.io.bundleAluControl.ctrlJAL.poke(false.B)
        dut.io.bundleAluControl.ctrlOP.poke(op)
        dut.io.bundleAluControl.ctrlSigned.poke(sign)
        dut.io.pc.poke(0.U)
        dut.io.imm.poke(0.U)
        dut.io.dataRead1.poke(a.U)
        dut.io.dataRead2.poke(b.U)
        val (resultAlu, resultBranch) = alu(a, b, op, sign)
        dut.io.resultAlu.expect((resultAlu.toLong & 0x00ffffffffL).U)
        dut.io.resultBranch.expect(resultBranch.B)
        // JAL+IMM
        dut.io.bundleAluControl.ctrlALUSrc.poke(true.B)
        dut.io.bundleAluControl.ctrlJAL.poke(true.B)
        dut.io.pc.poke(a.U)
        dut.io.imm.poke(b.U)
        dut.io.dataRead1.poke(0.U)
        dut.io.dataRead2.poke(0.U)
        val (resultAluJAL, resultBranchJAL) = alu(a, b, op, sign)
        dut.io.resultAlu.expect((resultAluJAL.toLong & 0x00ffffffffL).U)
        dut.io.resultBranch.expect(resultBranchJAL.B)
        // IMM
        dut.io.bundleAluControl.ctrlALUSrc.poke(true.B)
        dut.io.bundleAluControl.ctrlJAL.poke(false.B)
        dut.io.pc.poke(0.U)
        dut.io.imm.poke(b.U)
        dut.io.dataRead1.poke(a.U)
        dut.io.dataRead2.poke(0.U)
        val (resultAluIMM, resultBranchIMM) = alu(a, b, op, sign)
        dut.io.resultAlu.expect((resultAluIMM.toLong & 0x00ffffffffL).U)
        dut.io.resultBranch.expect(resultBranchIMM.B)
    }

    // 遍历功能和操作数进行测试
    def testFn(dut: Alu): Unit = {
        for (a <- oprand_list) {
            for (b <- oprand_list) {
                for (op <- OP_TYPES_LIST) {
                    testOne(dut, a, b, op, true)
                    testOne(dut, a, b, op, false)
                }
            }
        }
    }
}

class AluTest extends AnyFlatSpec with ChiselScalatestTester with AluTestFunc {
    "ALU" should "pass" in {
        test(new Alu) { dut =>
            testFn(dut)
        }
    }
}
