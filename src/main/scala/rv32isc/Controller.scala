package rv32isc

import chisel3._
import chisel3.util._

import utils._

class ControllerIO extends Bundle {
    val bundleControlIn = Flipped(new BundleControl()) // 来自译码器
    val bundleAluControl = Flipped(new BundleAluControl())  // 到ALU
    val bundleMemDataControl = Flipped(new BundleMemDataControl())  // 到数据内存
    val bundleControlOut = new BundleControl()  // 到其他
}

class Controller extends Module {
    val io = IO(new ControllerIO)

    // alu
    io.bundleAluControl.ctrlALUSrc := io.bundleControlIn.ctrlALUSrc
    io.bundleAluControl.ctrlJAL := io.bundleControlIn.ctrlJAL
    io.bundleAluControl.ctrlOP := io.bundleControlIn.ctrlOP
    io.bundleAluControl.ctrlSigned := io.bundleControlIn.ctrlSigned
    io.bundleAluControl.ctrlBranch := io.bundleControlIn.ctrlBranch

    // 内存单元
    io.bundleMemDataControl.ctrlLSType := io.bundleControlIn.ctrlALUSrc
    io.bundleMemDataControl.ctrlLoad := io.bundleControlIn.ctrlLoad
    io.bundleMemDataControl.ctrlSigned := io.bundleControlIn.ctrlSigned
    io.bundleMemDataControl.ctrlStore := io.bundleControlIn.ctrlStore
    
    // 其他
    io.bundleControlOut <> io.bundleControlIn
}