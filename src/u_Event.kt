package jm.lib

import kotlin.reflect.KProperty

// ---------------------------------------------------------------------------------------
/**Храниличе функций обратного вызова для функции **без параметров**.
 * Может хранить одновременно функцию и интерфейс и при [invoke] вызовет их обоих.
 *
 * Параметры шаблона описывают тип интерфейса и тип параметров функций обратного вызова
 * - Event&lt;InterfaceClass>{ call interface method }
 * - Event1&lt;InterfaceClass,ParamType1>{ ParamType1 -> call interface method }
 * - Event2&lt;InterfaceClass,ParamType1,ParamType2>{ ParamType1,ParamType2 -> call interface method }
 *
 * Методы
 * - delCB - удаление функционального калбака
 * - delListener - удаление калбака интерфейса
 * - clear - удаление всех обратных вызовов
 * - invoke - вызывает установленные функции обратного вызова
 * - += - устанавливает функцию или интерфейс
 *
 * Пример
 *```
 * //without parameters
 * interface Intf { fun Call() }         //interface
 * val OnCall = Event&lt;Intf>{ Call() } //define callback
 * OnCall += { DoActionsOnCalled() }     //set func in callback
 * OnCall.invoke()                       //call callback func
 *
 * //Complex event
 * val onPositionTouch =
 *   Event3&lt;OnPositionListener, HueGradientView, Float, Float>
 *   { v, x, y -> OnPositionTouch(v, x, y) }
 * onPositionTouch.invoke(this, x, y) //call
 * colors.onPositionTouch += this //setup interface callback
 *```
 */
open class Event<T>( private val ic: T.()->Unit ) {
  protected var l : T? = null
  protected var f : (()->Unit)? = null
  fun delCB() { f = null }
  fun delListener() { l = null }
  fun clear() { f = null; l = null }

  operator fun plusAssign( cb : (()->Unit)? ) { f = cb }
  operator fun plusAssign( cb : T? ) { l = cb }

  fun invoke() {
    l?.ic()
    f?.invoke()
  }
}

/**Храниличе функций обратного вызова для функции **с одним параметром**.
 * @see Event
 */
open class Event1<T,P1>( private val ic: T.(p1:P1)->Unit ) {
  protected var l : T? = null
  protected var f : ((P1)->Unit)? = null
  fun delCB() { f = null }
  fun delListener() { l = null }
  fun clear() { f = null; l = null }

  operator fun plusAssign( cb : ((P1)->Unit)? ) { f = cb }
  operator fun plusAssign( cb : T? ) { l = cb }

  fun invoke(p1:P1) {
    l?.ic(p1)
    f?.invoke(p1)
  }
}

/**Храниличе функций обратного вызова для функции **с двумя параметрами**.
 * @see Event
 */
open class Event2<T,P1,P2>( private val ic: T.(p1:P1,P2)->Unit ) {
  protected var l : T? = null
  protected var f : ((P1,P2)->Unit)? = null
  fun delCB() { f = null }
  fun delListener() { l = null }
  fun clear() { f = null; l = null }

  operator fun plusAssign( cb : ((P1,P2)->Unit)? ) { f = cb }
  operator fun plusAssign( cb : T? ) { l = cb }

  fun invoke(p1:P1,p2:P2) {
    l?.ic(p1,p2)
    f?.invoke(p1,p2)
  }
}

/**Храниличе функций обратного вызова для функции **с тремя параметрами**.
 * @see Event
 */
open class Event3<T,P1,P2,P3>( private val ic: T.(p1:P1,P2,P3)->Unit ) {
  private var l : T? = null
  private var f : ((P1,P2,P3)->Unit)? = null
  fun delCB() { f = null }
  fun delListener() { l = null }
  fun clear() { f = null; l = null }

  operator fun plusAssign( cb : ((P1,P2,P3)->Unit)? ) { f = cb }
  operator fun plusAssign( cb : T? ) { l = cb }

  fun invoke(p1:P1,p2:P2,p3:P3) {
    l?.ic(p1,p2,p3)
    f?.invoke(p1,p2,p3)
  }
}

private class EventTest {
  class A {
    interface Intf {
      fun Call()
      fun CallWidthA(a: A)
    }

    val OnEvent0 = Event<A.Intf> { Call() }
    val OnEvent1 = Event1<A.Intf, A> { p1 -> CallWidthA(p1) }

    private fun DoCall() {
      OnEvent0.invoke()
      OnEvent1.invoke(this)
    }
  }

  class B {
    fun idCall() {
    }

    @Suppress("UNUSED_PARAMETER")
    fun idCallWidthA(a: A) {
    }

    fun intf(a: A) {
      a.OnEvent0 += { idCall() }
      a.OnEvent1 += { a -> idCallWidthA(a) }

      a.OnEvent0.clear()
      a.OnEvent1.delCB()
    }
  }
}
// ---------------------------------------------------------------------------------------
