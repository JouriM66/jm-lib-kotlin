package jm.lib

import kotlin.reflect.KProperty

// ---------------------------------------------------------------------------------------
/** Value stored in class and marked as 'changed' on assignment.
 *  Can be used to filer out 'unchanged' values after mass assignment like options change.
 *
 *  - ChangeableBase - base class with all interface
 *  - [ChangeableValue] - contains value, it cannot be NULL
 *  - [ChangeableLazy] - value with lazy initialization (at first 'get' call), value can be NULL
 *
 *Sample:
 *```
 *fun run() {
 * //Clone
 * val v = ChangeableValue(10)
 * val cl = v.toValue()
 * cl.set(5)
 *
 * assert(v.get() == 10)
 * assert(v.changed() == false)
 * assert(cl.get() == 5)
 * assert(cl.changed() == true)
 *
 * cl.set(10)
 * assert(cl.get() == 10)
 * assert(cl.changed() == false)
 *
 * //Defaults
 * val a = ChangeableValue(10,15)
 * a.set(5)
 * assert(a.get() == 5)
 * assert(a.changed() == true)
 * a.set(15)
 * assert(a.get() == 15)
 * assert(a.changed() == false)
 * }
 *
 *  ```
 */
abstract class ChangeableBase<T> {
  protected var Fchanged : Boolean = false
  protected var value : T? = null
  protected var default : T? = null

  constructor()
  constructor(v : T) {
    value = v
  }

  protected abstract fun do_get() : T

  operator fun getValue(tRef : Any?, property : KProperty<*>) : T = get()
  operator fun setValue(thisRef : Any?, property : KProperty<*>, v : T) = set(v)

  fun Reset() {
    Fchanged = false
  }

  val changed : Boolean
    get() {
      if (!changed || value == null) return false
      if (default != null)
        return value!! != default
      else
        return true
    }

  /**Create changeable clone with current value as default
   * Note: Traps if value is NULL
   */
  fun toValue() = ChangeableValue<T>(value as T, default)

  /**Set new value and mark it changed
   */
  fun set(src : ChangeableBase<T>) {
    value = src.value; Fchanged = src.changed; }

  fun set(v : T) : T {
    Fchanged = true; value = v; return v; }

  /**Get value
   */
  fun get() : T = do_get()

  /**Call callback if value was changed, resets 'changed'
   */
  fun IfChange(cb : (T) -> Unit) {
    if (changed) {
      cb(get())
      Reset()
    }
  }

  companion object {
    /**Syntax and usage test*/
    fun Text() {
      //Clone
      val v = ChangeableValue(10)
      val cl = v.toValue()
      cl.set(5)

      assert(v.get() == 10)
      assert(v.changed == false)
      assert(cl.get() == 5)
      assert(cl.changed == true)

      cl.set(10)
      assert(cl.get() == 10)
      assert(cl.changed == false)

      //Defaults
      val a = ChangeableValue(10, 15)
      a.set(5)
      assert(a.get() == 5)
      assert(a.changed == true)
      a.set(15)
      assert(a.get() == 15)
      assert(a.changed == false)
    }
  }
}

/**
 * [ChangeableBase<T>] descendant to maintain non-NULL-able values, created in place
 */
class ChangeableValue<T>(v : T, def : T? = null) : ChangeableBase<T>(v) {

  override fun do_get() : T = value as T
}

/**
 * [ChangeableBase<T>] descendant to maintain NULL-able values, created by lazy called callback
 */
class ChangeableLazy<T>(val init : () -> T) : ChangeableBase<T>() {
  override fun do_get() : T {
    if (value == null) value = init(); return value!!; }
}
