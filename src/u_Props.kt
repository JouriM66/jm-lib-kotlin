package jm.lib

import kotlin.reflect.KProperty

abstract class ValueAccessBase {
  interface Getter {
    fun Getted()
  }

  interface Setter {
    fun Setted(owner : Any?)
  }
}

open class LazyVal<T> : ValueAccessBase.Getter {
  protected var value : T? = null
  protected var init : (() -> T)? = null

  constructor(cb : () -> T) {
    init = cb
  }

  constructor(v : T) {
    value = v
  }

  operator fun getValue(tRef : Any?, property : KProperty<*>) : T = get()
  override fun Getted() {
    if (value == null) value = init!!()
  }

  fun get() : T {
    Getted()
    return value!!
  }
}

open class LazyVar<T> : LazyVal<T>, ValueAccessBase.Setter {
  constructor(v : T) : super(v)
  constructor(v : () -> T) : super(v)

  operator fun setValue(tRef : Any?, property : KProperty<*>, v : T) : T = set(v, tRef)

  override fun Setted(owner : Any?) {}

  protected open fun Check(v : T) : T = v

  fun set(v : T, owner : Any? = null) : T {
    val cv = Check(v)
    if (cv == get()) return cv
    value = cv
    Setted(owner)
    return value!!
  }

  /**Call setter with current property value without any checks.
   * @param other View descendant, property owner
   */
  fun callSet(owner : Any? = null) {
    Setted(owner)
  }
}

// ---------------------------------------------------------------------------------------
// PROPERTY
// ---------------------------------------------------------------------------------------
/** RO Property delegate which stores property in local field
 * @see PropP
 * @see PropVV
 * @see PropVP
 * @see PropPP */
class PropV<T> : LazyVal<T> {
  constructor(v : T) : super(v)
  constructor(v : () -> T) : super(v)
}

/** RO property delegate wich call user callback to get prop value
 * @see PropV
 * @see PropVV
 * @see PropVP
 * @see PropPP */
class PropP<T>(getter : () -> T) : LazyVal<T>(getter) {
  override fun Getted() {
    value = init!!()
  }
}

/** RW Property delegate which stores property in local field
 * @see PropP
 * @see PropVV
 * @see PropVP
 * @see PropPP */
class PropVV<T> : LazyVar<T> {
  constructor(v : T) : super(v)
  constructor(v : () -> T) : super(v)
}

/** RW Property delegate which stores property in local field and call
 * user callback on property set
 * Optionally allow to set callback to correct value before it will be set (correct value bounds f.i.)
 * To call setter without checks use [callSet] method
 * @see PropP
 * @see PropVV
 * @see PropVP
 * @see PropPP */
open class PropVP<T> : LazyVar<T> {
  protected val setter : (value : T) -> Unit
  protected val checker : ((value : T) -> T)?

  constructor(value : T, setter : (v : T) -> Unit)
    : super(value) {
    this.setter = setter; checker = null
  }

  constructor(value : T, checker : (v : T) -> T, setter : (v : T) -> Unit)
    : super(value) {
    this.setter = setter; this.checker = checker
  }

  constructor(v : () -> T, setter : (v : T) -> Unit)
    : super(v) {
    this.setter = setter; checker = null
  }

  constructor(v : () -> T, checker : (v : T) -> T, setter : (v : T) -> Unit)
    : super(v) {
    this.setter = setter; this.checker = checker
  }

  override fun Check(v : T) : T = checker?.invoke(v) ?: v
  override fun Setted(owner : Any?) {
    setter(value!!)
  }
}

/**RW Property delegate which call user callbacks on property read and set.
 *
 * Optionally allow to set callback to correct value before it will be set (correct value bounds f.i.)
 * @see PropP
 * @see PropVV
 * @see PropVP
 * @see PropPP */
class PropPP<T> : PropVP<T> {
  constructor(getter : () -> T, setter : (v : T) -> Unit)
    : super(getter, setter)

  constructor(getter : () -> T, checker : (v : T) -> T, setter : (v : T) -> Unit)
    : super(getter, checker, setter)

  override fun Getted() {
    value = init!!()
  }
}

