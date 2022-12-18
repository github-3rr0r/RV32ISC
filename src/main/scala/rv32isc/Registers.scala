package rv32isc

import chisel3._
import chisel3.util._

import config.Configs._
import utils._

class RegistersIO extends Bundle {
    val ctrlRegWrite = Input(Bool())
    val ctrlJump = Input(Bool())
    val pc = Input(UInt(ADDR_WIDTH.W))
    val dataWrite = Input(UInt(DATA_WIDTH.W))
    val bundleReg = Flipped(new BundleReg)
    val dataRead1 = Output(UInt(DATA_WIDTH.W))
    val dataRead2 = Output(UInt(DATA_WIDTH.W))
}

class Registers extends Module {
    val io = IO(new RegistersIO())

    // 寄存器组，REG_NUMS个，位宽DATA_WIDTH
    val regs = Reg(Vec(REG_NUMS, UInt(DATA_WIDTH.W)))

    // 寄存器号为0时读到0
    when(io.bundleReg.rs1 === 0.U) {
        io.dataRead1 := 0.U
    }
    when(io.bundleReg.rs2 === 0.U) {
        io.dataRead2 := 0.U
    }
    // 否则给出数据
    io.dataRead1 := regs(io.bundleReg.rs1)
    io.dataRead2 := regs(io.bundleReg.rs2)
    // 给出写信号，且rd不为0时写寄存器
    when(io.ctrlRegWrite && io.bundleReg.rd =/= 0.U) {
        when(io.ctrlJump) {
            regs(io.bundleReg.rd) := io.pc + INST_BYTE_WIDTH.U
        }.otherwise {
            regs(io.bundleReg.rd) := io.dataWrite
        }
    }
}
