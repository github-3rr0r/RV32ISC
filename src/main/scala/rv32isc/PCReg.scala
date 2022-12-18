package rv32isc

import chisel3._
import chisel3.util._

import config.Configs._

// PCReg的模块接口
class PCRegIO extends Bundle {
    val addrOut = Output(UInt(ADDR_WIDTH.W))      // 地址输出
    val ctrlJump = Input(Bool())                // 当前指令是否为跳转指令
    val ctrlBranch = Input(Bool())              // 当前指令是否为分支指令
    val resultBranch = Input(Bool())            // 分支结果是否为分支成功
    val addrTarget = Input(UInt(ADDR_WIDTH.W))    // 跳转/分支的目的地址
}

// PCReg模块
class PCReg extends Module {
    val io = IO(new PCRegIO())  // 输入输出接口

    val regPC = RegInit(UInt(ADDR_WIDTH.W), START_ADDR.U)   // PC寄存器，初始化时重置为START_ADDR

    when (io.ctrlJump || (io.ctrlBranch && io.resultBranch)) {  // 跳转或分支成功时，更新为目的地址
        regPC := io.addrTarget
    } .otherwise {  // 否则自增4
        regPC := regPC + ADDR_BYTE_WIDTH.U
    }

    io.addrOut := regPC // 每个时钟周期输出当前PC寄存内的地址
}