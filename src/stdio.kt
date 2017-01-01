package jm.lib

import java.io.OutputStream

// ---------------------------------------------------------------------------------------
class NullOutputStream : OutputStream {
  private constructor() : super()

  private fun nonev() {}
  override fun write(b : Int) = nonev()
  override fun write(b : ByteArray?) = nonev()
  override fun write(b : ByteArray?, off : Int, len : Int) = nonev()
  override fun flush() = nonev()
  override fun close() = nonev()

  companion object {
    val instance by lazy {
      NullOutputStream()
    }
  }
}

