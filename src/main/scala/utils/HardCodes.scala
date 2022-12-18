package utils

import chisel3._

object OP_TYPES {
    val OP_TYPES_WIDTH = 4
    val OP_NOP = "b0000".U
    val OP_ADD = "b0001".U
    val OP_SUB = "b0010".U
    val OP_AND = "b0100".U
    val OP_OR = "b0101".U
    val OP_XOR = "b0111".U
    val OP_SLL = "b1000".U
    val OP_SRL = "b1001".U
    val OP_SRA = "b1011".U
    val OP_EQ = "b1100".U
    val OP_NEQ = "b1101".U
    val OP_LT = "b1110".U
    val OP_GE = "b1111".U
}

object LS_TYPES {
    val LS_TYPE_WIDTH = 2
    val LS_B = "b00".U
    val LS_H = "b01".U
    val LS_W = "b10".U
}