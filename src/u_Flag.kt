package jm.lib

import kotlin.reflect.KProperty

// ---------------------------------------------------------------------------------------
open class Flag {
  private var bits : Long = 0
  private var nBit = 0
  @JvmField val flags = mutableMapOf<Long, FlagBit>()

  constructor(v : Long = 0) {
    bits = v
  }

  constructor(v : BitBase) {
    bits = v.value
  }

  open class BitBase(@JvmField val value : Long, @JvmField val name : String) {

    override fun toString() : String = name
    override fun hashCode() : Int = name.hashCode()
    override fun equals(other : Any?) : Boolean = value == (other as? FlagBit)?.value ?: 0

    operator fun plus(p : BitBase) : BitBase = BitBase(value or p.value, "")
    operator fun minus(p : BitBase) : BitBase = BitBase(value and p.value.inv(), "")
  }

  class FlagBit(v : Long, nm : String, @JvmField val parent : Flag) : BitBase(v, nm) {

    operator fun getValue(tRef : Any?, pr : KProperty<*>) : Boolean = get()
    operator fun setValue(tRef : Any?, pr : KProperty<*>, v : Boolean) = if (v) set() else clr()

    fun set() = parent.SET_FLAG(this)

    fun get() = parent.IS_FLAG(this)
    fun clr() = parent.CLR_FLAG(this)
    fun inv() = parent.INV_FLAG(this)
  }

  private fun createFlag(v : Long, nm : String) : FlagBit {
    var b = flags.get(v)
    if (b != null) return b
    b = FlagBit(v, nm, this)
    flags.put(v, b)
    return b
  }

  fun newFlag(b : BitBase, nm : String = "") = createFlag(b.value, nm.ifEmpty { b.toString() })
  fun newFlag(bit : Long, nm : String = "") = createFlag(bit, nm)
  fun newFlag(nm : String = "") =
    if (nBit >= 63) throw RuntimeException("Flags capacity overflow") else
      createFlag(1L shl nBit++, nm)

  companion object {
    @JvmStatic
    fun newFlag(bit : Long, nm : String = "") = BitBase(bit, nm)
    fun newFlag(b : BitBase, nm : String = "") = BitBase(b.value, nm)
  }

  fun set(v : Long) : Flag {
    bits = v; return this
  }

  val value : Long get() = bits
  fun isEmpty() : Boolean = value == 0L
  fun isNotEmpty() : Boolean = value != 0L
  fun clear() = set(0)
  fun all() = set(-1L)
  fun namedFlags(case:Boolean = false) : Map<String, FlagBit> =
    flags.mapKeys { if (case) it.value.name else it.value.name.toUpperCase() }

  fun IS_FLAG(v : BitBase) = IS_FLAG(v.value)
  fun SET_FLAG(v : BitBase) = SET_FLAG(v.value)
  fun CLR_FLAG(v : BitBase) = CLR_FLAG(v.value)
  fun INV_FLAG(v : BitBase) = INV_FLAG(v.value)

  fun INV_FLAG(v : Long) = if (IS_FLAG(v)) CLR_FLAG(v) else SET_FLAG(v)
  fun IS_FLAG(v : Long) : Boolean = (bits and v) == v
  fun SET_FLAG(v : Long) = set(bits or v)
  fun CLR_FLAG(v : Long) = set(bits and v.inv())

  override fun toString() : String = toString('|')
  fun toString(separator : Char ) : String =
    flags
      .filter { it.value.get() }
      .map { it.value.toString() }
      .joinToString(separator = separator.toString())

  fun fromString(s : String, sep : Char = '|', case : Boolean = false) =
    fromList(
      s.split(sep)
        .map { val v = it.trim(); if (case) v else v.toUpperCase() }
        .filter { it.isNotBlank() }
    )

  fun fromList(ar : List<String>,case : Boolean = false) {
    val nm = namedFlags(case)
    var v = 0L
    ar.forEach {
      val p = nm.get(it)
      if (p != null) v += p.value
    }
    set(v)
  }
}
