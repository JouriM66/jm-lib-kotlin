package jm.lib

import kotlin.reflect.KProperty

// ---------------------------------------------------------------------------------------
/**Value holder with read only access, nullable, optional lazy inited.
 * Its sad Kotlin dosnt allow to overload type casting, so its need to use set\get methods
 * to get value or use delegations.
 *
 * See [Test] for example.
 * @see AnyTypeReadWrite
 * @see AnyOrdinal
 * @see AnyString
 * @see AnyDouble
 */
open class AnyTypeReadOnly<T> {
  protected var value : T?
  protected val init : (() -> Any?)? //It must be Any type to allow use base classes

  constructor(v : T? = null) {
    value = v; init = null
  }

  constructor(cb : () -> Any?) {
    value = null; init = cb
  }

  protected open fun GetZeroVal() : T? = null
  @Suppress("UNCHECKED_CAST")
  protected open fun Initialize() : T? = init?.invoke() as T?

  /**Initialize value if need and return it.<br>
   *
   * Throws exception if stored value not initialized or unconvertable to T
   */
  open fun get() : T {
    if (value == null) {
      value = Initialize() ?: GetZeroVal()
      if (value == null)
        throw NullPointerException("Initialization failed")
    }
    return value as T
  }

  operator fun getValue(tRef : Any?, property : KProperty<*>) : T {
    if (value == null) {
      value = Initialize() ?: GetZeroVal()
      if (value == null)
        throw NullPointerException("Initialization failed")
    }

    if (value !is T) {
      val src = value?.toString()?.padEnd(30) ?: "<null>"
      throw ClassCastException("!cast: $src -> $property.name")
    }
    return value as T
  }

  //Test
  companion object {
    /**Syntax and usage test*/
    fun Test() {
      //Simple types
      var i : Int = 99
      assert(i == 99)

      val n = AnyTypeReadOnly<Long>(10)
      i = n.get().toInt()
      assert(i == 10)

      val n1 = AnyTypeReadOnly<Int> { 15 }
      i = n1.get()
      assert(i == 15)

      //Compound objects
      class CompoundObject {
        val field = 18
        fun Method() : Int = 55
      }

      //lazy init
      val v = AnyTypeReadOnly<CompoundObject> { CompoundObject() }
      v.get().Method()

      //Delegation with lazy init
      class AA {
        val field by AnyTypeReadOnly<CompoundObject> { CompoundObject() }
      }
      AA().field.Method()
    }
  }
}


/**Just short name for [AnyTypeReadOnly]
 */
open class AnyVal<T> : AnyTypeReadOnly<T> {
  constructor(v : T? = null) : super(v)
  constructor(cb : () -> Any?) : super(cb)
}

/**Read\write descendant of [AnyTypeReadOnly]
 * Can be used as delegate.
 * See [Test] for example.
 * @see AnyVar
 * @see AnyOrdinal
 * @see AnyString
 * @see AnyDouble
 */
open class AnyTypeReadWrite<T> : AnyTypeReadOnly<T> {
  constructor(v : T? = null) : super(v)
  constructor(cb : () -> Any?) : super(cb)

  /** Sets value. Do not check it type or content! Descendants can provide additional checks.
   *
   * Its possible to assign null. In this case value will be initialized on next [get] call
   *
   * Throws unconvertable exception.
   */
  @Suppress("UNCHECKED_CAST")
  open fun set(v : Any?) : T {
    value = v as? T; return get()
  }

  operator fun setValue(thisRef : Any?, property : KProperty<*>, v : T?) = set(v)

  //Test
  companion object {
    /**Syntax and usage test*/
    fun Test() {
      //int checker
      var i : Int = 11
      assert(i == 11)

      //value asigned
      val v = AnyTypeReadWrite<Int>(10)
      i = v.get(); //set i to 10
      assert(i == 10)

      val v1 = AnyTypeReadWrite<Int> { 15 }
      i = v1.get(); //set i to 15
      assert(i == 15)

      //value delegated
      class AA {
        var field : Int by AnyTypeReadWrite<Int>(16)
      }

      val a = AA()
      i = a.field
      assert(i == 16)

      //Nullable value
      val vx = AnyTypeReadWrite<Int>()
      vx.get(); //throw exception as value not initialized
    }
  }
}

/**Just short name for [AnyTypeReadWrite]
 */
open class AnyVar<T> : AnyTypeReadWrite<T> {
  constructor(v : T? = null) : super(v)
  constructor(cb : () -> Any?) : super(cb)
}

// ---------------------------------------------------------------------------------------
/** Ordinal data type [AnyVar] used for transparent types conversion Kotlin dosnt have by default.
 * Base type for ordinal is Long (64-bit). See [asOrdinal].
 */
open class AnyOrdinal : AnyVar<Long> {
  constructor(v : Long?) : super(v)
  constructor(v : Int) : super(v.asOrdinal())
  constructor(v : Boolean) : super(v.asOrdinal())
  constructor(v : AnyVar<Any>) : super(v.get().asOrdinal())
  constructor(cb : () -> Long) : super(cb)

  override fun GetZeroVal() : Long = 0
  override fun set(v : Any?) : Long = super.set(v?.asOrdinal() ?: 0)
}

/** String data type [AnyVar] with transparent types conversion (see [asString])
 */
open class AnyString : AnyVar<String> {
  constructor(v : Any?) : super(v?.asString())
  constructor(v : AnyVar<Any>) : super(v.get().asString())
  constructor(cb : () -> String) : super(cb)

  override fun GetZeroVal() : String = ""
  override fun set(v : Any?) : String = super.set(v?.asString() ?: "")
}

/** Fixed-point data type [AnyVar] with transparent types conversion (see [asDouble])
 */
open class AnyDouble : AnyVar<Double> {
  constructor(v : Any?) : super(v?.asDouble())
  constructor(v : AnyVar<Any>) : super(v.get().asDouble())
  constructor(cb : () -> Double) : super(cb)

  override fun GetZeroVal() : Double = 0.0
  override fun set(v : Any?) : Double = super.set(v?.asDouble() ?: 0.0)
}
// ---------------------------------------------------------------------------------------
