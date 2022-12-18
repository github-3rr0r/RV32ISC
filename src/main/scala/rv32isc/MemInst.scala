package rv32isc

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

import config.Configs._
import firrtl.annotations.MemoryLoadFileType

class MemInstIO extends Bundle {
    val addr = Input(UInt(ADDR_WIDTH.W)) // 指令地址
    val inst = Output(UInt(INST_WIDTH.W)) // 指令输出
}

class MemInst(memTest: Boolean = false) extends Module {
    val io = IO(new MemInstIO()) // 输入输出接口

    // 指令内存，能存放MEM_INST_SIZE条INST_WIDTH位的指令
    val mem = Mem(MEM_INST_SIZE, UInt(INST_WIDTH.W))

    if (memTest) {
        loadMemoryFromFile(
            mem,
            "src/test/scala/rv32isc/randMemInst.hex",
            MemoryLoadFileType.Hex
        )
    } else {
        loadMemoryFromFile(
            mem,
            "src/test/scala/rv32isc/MemInst.hex",
            MemoryLoadFileType.Hex
        )
    }
    io.inst := mem.read(io.addr >> INST_BYTE_WIDTH_LOG.U) // 读取对应位置的指令并输出
}
