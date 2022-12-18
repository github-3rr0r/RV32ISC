package rv32isc

import chisel3._
import chisel3.util._

import config.Configs._
import utils.OP_TYPES._
import utils._

class AluIO extends Bundle {
    val bundleAluControl = new BundleAluControl()
    val dataRead1 = Input(UInt(DATA_WIDTH.W))
    val dataRead2 = Input(UInt(DATA_WIDTH.W))
    val imm = Input(UInt(DATA_WIDTH.W))
    val pc = Input(UInt(ADDR_WIDTH.W))
    val resultBranch = Output(Bool())
    val resultAlu = Output(UInt(DATA_WIDTH.W))
}

class Alu extends Module {
    val io = IO(new AluIO())

    // 用于输出比较结果和计算结果
    val resultBranch = WireDefault(false.B)
    val resultAlu = WireDefault(0.U(DATA_WIDTH.W))

    // 用于得到操作数
    val oprand1 = WireDefault(0.U(DATA_WIDTH.W))
    val oprand2 = WireDefault(0.U(DATA_WIDTH.W))

    oprand1 := Mux(io.bundleAluControl.ctrlJAL, io.pc, io.dataRead1)
    oprand2 := Mux(io.bundleAluControl.ctrlALUSrc, io.imm, io.dataRead2)

    // 根据bundleAluControl中的信号进行选择
    switch(io.bundleAluControl.ctrlOP) {
        is(OP_NOP) { // 啥也不干
            resultAlu := 0.U
            resultBranch := false.B
        }
        is(OP_ADD) {
            resultAlu := oprand1 +& oprand2
        }
        is(OP_SUB) {
            resultAlu := oprand1 -& oprand2
        }
        is(OP_AND) {
            resultAlu := oprand1 & oprand2
        }
        is(OP_OR) {
            resultAlu := oprand1 | oprand2
        }
        is(OP_XOR) {
            resultAlu := oprand1 ^ oprand2
        }
        is(OP_SLL) {
            resultAlu := oprand1 << oprand2(4, 0)
        }
        is(OP_SRL) {
            resultAlu := oprand1 >> oprand2(4, 0)
        }
        is(OP_SRA) { // 需要注意算术右移的写法
            resultAlu := (oprand1.asSInt >> oprand2(4, 0)).asUInt
        }
        is(OP_EQ) {
            resultBranch := oprand1.asSInt === oprand2.asSInt
            resultAlu := io.pc +& io.imm
        }
        is(OP_NEQ) {
            resultBranch := oprand1.asSInt =/= oprand2.asSInt
            resultAlu := io.pc +& io.imm
        }
        is(OP_LT) { // 区分有符号比较和无符号比较、分支和SLT
            when(io.bundleAluControl.ctrlBranch) {
                when(io.bundleAluControl.ctrlSigned) {
                    resultBranch := oprand1.asSInt < oprand2.asSInt
                }.otherwise {
                    resultBranch := oprand1 < oprand2
                }
                resultAlu := io.pc +& io.imm
            }.otherwise {
                when(io.bundleAluControl.ctrlSigned) {
                    resultAlu := oprand1.asSInt < oprand2.asSInt
                }.otherwise {
                    resultAlu := oprand1 < oprand2
                }
            }
        }
        is(OP_GE) { // 区分有符号比较和无符号比较
            when(io.bundleAluControl.ctrlSigned) {
                resultBranch := oprand1.asSInt >= oprand2.asSInt
            }.otherwise {
                resultBranch := oprand1 >= oprand2
            }
            resultAlu := io.pc +& io.imm
        }
    }

    io.resultAlu := resultAlu
    io.resultBranch := resultBranch
}