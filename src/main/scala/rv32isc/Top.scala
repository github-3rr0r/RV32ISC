package rv32isc

import chisel3._
import chisel3.util._

import config.Configs._
import utils._

// Top的模块接口，用于测试
class TopIO extends Bundle {
    val addr = Output(UInt(ADDR_WIDTH.W))
    val inst = Output(UInt(INST_WIDTH.W))
    val bundleCtrl = new BundleControl()
    val resultALU = Output(UInt(DATA_WIDTH.W))
    val rs1 = Output(UInt(DATA_WIDTH.W))
    val rs2 = Output(UInt(DATA_WIDTH.W))
    val imm = Output(UInt(DATA_WIDTH.W))
    val resultBranch = Output(Bool())
    val result = Output(UInt(DATA_WIDTH.W))
}

class Top extends Module {
    val io = IO(new TopIO())

    val pcReg = Module(new PCReg())
    val memInst = Module(new MemInst())
    val decoder = Module(new Decoder())
    val registers = Module(new Registers())
    val alu = Module(new Alu())
    val memData = Module(new MemData())
    val controller = Module(new Controller())

    // PCReg in
    pcReg.io.resultBranch <> alu.io.resultBranch
    pcReg.io.addrTarget <> memData.io.result
    pcReg.io.ctrlBranch <> controller.io.bundleControlOut.ctrlBranch
    pcReg.io.ctrlJump <> controller.io.bundleControlOut.ctrlJump
    
    // MemInst in
    memInst.io.addr <> pcReg.io.addrOut

    // Decoder in
    decoder.io.inst <> memInst.io.inst

    // Registers in
    registers.io.bundleReg <> decoder.io.bundleReg
    registers.io.ctrlRegWrite <> controller.io.bundleControlOut.ctrlRegWrite
    registers.io.ctrlJump <> controller.io.bundleControlOut.ctrlJump
    registers.io.dataWrite <> memData.io.result
    registers.io.pc <> pcReg.io.addrOut

    // ALU in
    alu.io.bundleAluControl <> controller.io.bundleAluControl
    alu.io.dataRead1 <> registers.io.dataRead1
    alu.io.dataRead2 <> registers.io.dataRead2
    alu.io.imm <> decoder.io.imm
    alu.io.pc <> pcReg.io.addrOut
    
    // MemData in
    memData.io.bundleMemDataControl <> controller.io.bundleMemDataControl
    memData.io.dataStore <> registers.io.dataRead2
    memData.io.resultALU <> alu.io.resultAlu

    // Controller in
    controller.io.bundleControlIn <> decoder.io.bundleCtrl
    
    // top
    io.addr <> pcReg.io.addrOut
    io.bundleCtrl <> decoder.io.bundleCtrl
    io.inst <> memInst.io.inst
    io.result <> memData.io.result
    io.resultALU <> alu.io.resultAlu
    io.resultBranch <> alu.io.resultBranch
    io.imm <> decoder.io.imm
    io.rs1 <> registers.io.dataRead1
    io.rs2 <> registers.io.dataRead2
}

object main extends App {
    println(getVerilogString(new Top()))
}