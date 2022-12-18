package rv32isc

import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec

import config.Configs._

trait DecoderTestFunc {
    def testFn(dut: Decoder): Unit = {
        // LUI x1, 0x1111
        // AUIPC x2, 0x2222
        // JAL x3, L0
        // L0:
        // JALR x4, x3, 4
        // L3:
        // BEQ x5, x6, L1
        // L1:
        // BNE x1, x2, L2
        // L2:
        // BLT x1, x2, L4
        // L4:
        // BGE x1, x2, L5
        // L5:
        // BLTU x1, x2, L6
        // L6:
        // BGEU x1, x2, L7
        // L7:
        // LB x1, 0x4, x2
        // LH x1, 0x8, x2
        // LW x1, 0x4, x2
        // LBU x1, 0x8, x2
        // LHU x1, 0xc, x2
        // SB x1, 0x4, x1
        // SH x1, 0x4, x1
        // SW x1, 0x4, x1
        // ADDI x1, x1, 0x4
        // SLTI x1, x1, 0x4
        // SLTIU x1, x1, 0x4
        // XORI x1, x1, 0x4
        // ORI x1, x1, 0x4
        // ANDI x1, x1, 0x4
        // SLLI x1, x1, 0x4
        // SRLI x1, x1, 0x4
        // SRAI x1, x1, 0x4
        // ADD x1, x1, x2
        // SUB x1, x1, x2
        // SLL x1, x1, x2
        // SLT x1, x1, x2
        // SLTU x1, x1, x2
        // XOR x1, x1, x2
        // SRL x1, x1, x2
        // SRA x1, x1, x2
        // OR x1, x1, x2
        // AND x1, x1, x2

        val inst_list = Seq(
            "h011110b7".U,
            "h02222117".U,
            "h004001ef".U,
            "h00418267".U,
            "h00628263".U,
            "h00209263".U,
            "h0020c263".U,
            "h0020d263".U,
            "h0020e263".U,
            "h0020f263".U,
            "h00410083".U,
            "h00811083".U,
            "h00412083".U,
            "h00814083".U,
            "h00c15083".U,
            "h00108223".U,
            "h00109223".U,
            "h0010a223".U,
            "h00408093".U,
            "h0040a093".U,
            "h0040b093".U,
            "h0040c093".U,
            "h0040e093".U,
            "h0040f093".U,
            "h00409093".U,
            "h0040d093".U,
            "h4040d093".U,
            "h002080b3".U,
            "h402080b3".U,
            "h002090b3".U,
            "h0020a0b3".U,
            "h0020b0b3".U,
            "h0020c0b3".U,
            "h0020d0b3".U,
            "h4020d0b3".U,
            "h0020e0b3".U,
            "h0020f0b3".U
        )

        def test_Decoder(dut: Decoder): Unit = {
            for (inst <- inst_list) {
                dut.io.inst.poke(inst)
                println(dut.io.bundleReg.rs1.peekInt())
                println(dut.io.bundleReg.rs2.peekInt())
                println(dut.io.imm.peek())
                println(dut.io.bundleReg.rd.peekInt())
                println(dut.io.bundleCtrl.peek())
            }
        }
    }
}

class DecoderTest
    extends AnyFlatSpec
    with ChiselScalatestTester
    with DecoderTestFunc {
    "Decoder" should "pass" in {
        test(new Decoder) { dut =>
            testFn(dut)
        }
    }
}
