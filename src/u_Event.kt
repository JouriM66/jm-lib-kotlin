package jm.lib

import kotlin.reflect.KFunction

/*Changes
 * 12.01.2017
 *  * Event classes replaced by new with diff interface and improved functionality
*/

// -------------------------------------------------------------------------
private val log by lazy { PLog(false) }

//@Suppress("TYPE_MISMATCH")
//@Suppress("UNCHECKED_CAST")

// -------------------------------------------------------------------------
interface CallSource

// -------------------------------------------------------------------------
/**Contains list af listeners of any type (interface implementation, static method, class method, anonymous function, or lambda)
 * and allow: add, remove and call them.
 *
 * U must use one of **EventX** classes, where ``X`` is number of additional parameters in your event.
 *
 * More details see in [jm_lib_EventTest] test class.
 *
 * **Methods**
 * * [add] ( callerClass, eventObject ) - add ``eventObject`` as new listener for ``callerClass``.
 * ``callerClass`` is used to identify object which set callback. Its used to remove listeners and to call closure methods
 * and must be set to any non-null class. ``eventObject`` may be any type of listener. Supported types are:
 *     - ::method - static method. ``callerClass`` can be set to any object.
 *     - class::method - closure class method. ``callerClass`` must be set to object used to call method with.
 *     - this::method - closure auto-reference used to call class methods. ``callerClass`` can be set to any object.
 *     - { [params]-> } - lambda function with correct params set. ``callerClass`` can be set to any object.
 *     - object : InterfaceType {} - event interface implementation. ``callerClass`` can be set to any object.
 *
 * * [size] : Int - get number of listeners in handler
 *
 * * [clear] () - remove all listeners
 *
 * * [remove] ( callerClass ) - remove all listeners registered by ``callerClass``. The same as **-=** operator.
 *
 * **Construct listener**
 * ```
 * class ClassWidthEvent : CallSource {  //Must implement CallSource
 *   interface InterfaceForEvent {
 *      fun EventMethod( source:CallSource [, add params] )
 *   }
 *
 *   val OnEvent = EventX( InterfaceForEvent::EventMethod )
 *
 *   fun fireMethod() {  OnEvent.call(this[, add params] ) }
 * }
 * ```
 *
 * **Set listener**
 * ```
 *class UsingClass( obj:ClassWidthEvent ) {
 *  fun idEvent( source:CallSource [, add params]  ) { /*Called from event*/ }
 *
 *  init {
 *   obj.add( this, UsingClass::idEvent ) //add listener as closure method
 *   obj.add( this ) { [add params ] ->  //add as lambda
 *      /*Called from event*/
 *   }
 * }
 *}
 * ```
 * **See**: [Event], [Event1], [Event2], [Event3], [Event4], [Event5]
 */
open class EventBase(lCB : Any) {
  private var listenerCB : KFunction<Unit>
  private var items = mutableListOf<EventCaller>()

  init {
    @Suppress("UNCHECKED_CAST")
    listenerCB = lCB as KFunction<Unit>
  }

  val size : Int get() = items.size
  fun clear() = items.clear()
  operator fun minusAssign(eventUser : Any) = remove(eventUser)

  fun remove(eventUser : Any) {
    log.log { "EV: del for $eventUser" }
    //items = items.filterTo(mutableListOf()) { it.caller != eventUser }
    items.filterInplace { it.caller != eventUser }
  }

  protected fun addCaller(c : EventCaller) : EventBase {
    log.log { "EV: ${c.caller} :: ${c.obj}" }
    items.add(c)
    return this
  }

  protected fun callMethods(src : CallSource, vararg methodArgs : Any?) {
    for (p in items)
      p.callMethods(listenerCB, src, *methodArgs)
  }

  //------------------------------------------------------------------
  abstract protected class EventCaller(val caller : Any, val obj : Any) {
    abstract fun callMethods(listenerObj : KFunction<Unit>, src : CallSource, vararg pm : Any?)
  }

  protected class ListenerCaller(caller : Any, obj : Any) : EventCaller(caller, obj) {
    override fun callMethods(listenerObj : KFunction<Unit>, src : CallSource, vararg pm : Any?) {
      log.log { "EV:  listener [$obj].[$listenerObj]([${pm.size}])" }
      listenerObj.call(obj, src, *pm)
    }
  }

  protected class ClosureCaller(caller : Any, obj : Any) : EventCaller(caller, obj) {
    @Suppress("UNCHECKED_CAST")
    override fun callMethods(listenerObj : KFunction<Unit>, src : CallSource, vararg pm : Any?) {
      log.log { "EV:  closure [$caller]::[$obj]([$src],[${pm.size}])" }
      (obj as KFunction<Unit>).call(caller, src, *pm)
    }
  }

  protected class MethodCaller(caller : Any, obj : Function<*>) : EventCaller(caller, obj) {
    @Suppress("UNCHECKED_CAST")
    override fun callMethods(listenerObj : KFunction<Unit>, src : CallSource, vararg pm : Any?) {
      log.log { "EV:  method [$obj]([$src],[${pm.size}])" }
      when (pm.size) {
        0    -> (obj as Function1<CallSource, Unit>).invoke(src)
        1    -> (obj as Function2<CallSource, Any?, Unit>).invoke(src, pm[0])
        2    -> (obj as Function3<CallSource, Any?, Any?, Unit>).invoke(src, pm[0], pm[1])
        3    -> (obj as Function4<CallSource, Any?, Any?, Any?, Unit>).invoke(src, pm[0], pm[1], pm[2])
        4    -> (obj as Function5<CallSource, Any?, Any?, Any?, Any?, Unit>).invoke(src, pm[0], pm[1], pm[2], pm[3])
        5    -> (obj as Function6<CallSource, Any?, Any?, Any?, Any?, Any?, Unit>).invoke(src, pm[0], pm[1], pm[2], pm[3], pm[4])
        else -> throw NotImplementedError("Call with ${pm.size} args not implemented! Please add it by yourself here")
      }
    }
  }
}

// -------------------------------------------------------------------------
/**Listeners collection for type **(CallSource)->Unit**. Usage see in [jm_lib_EventTest.TestUserVoid]
 * @see EventBase*/
class Event<in LT : Any>(listenerEventMethod : LT.(CallSource) -> Unit) : EventBase(listenerEventMethod) {

  fun call(Source : CallSource) =
    callMethods(Source)

  fun <T : Any> add(eventUser : T, listenerObject : LT)
    = addCaller(ListenerCaller(eventUser, listenerObject))

  fun <T : Any, S : CallSource> add(eventUser : T, closureMethod : Function1<S, Unit>) =
    addCaller(ClosureCaller(eventUser, closureMethod))

  fun <T : Any, S : CallSource> add(eventUser : T, functionMethod : Function2<T, S, Unit>) =
    addCaller(MethodCaller(eventUser, functionMethod))
}

// -------------------------------------------------------------------------
/**Listeners collection for type **(CallSource,p1)->Unit**. Usage see in [jm_lib_EventTest.TestUser1]
 * @see EventBase*/
class Event1<in LT : Any, P1>(listenerEventMethod : LT.(CallSource, P1) -> Unit) : EventBase(listenerEventMethod) {

  fun call(Source : CallSource, p1 : P1) =
    callMethods(Source, p1)

  fun <T : Any> add(eventUser : T, listenerObject : LT) = addCaller(ListenerCaller(eventUser, listenerObject))

  fun <T : Any, S : CallSource> add(eventUser : T, closureMethod : Function2<S, P1, Unit>) =
    addCaller(ClosureCaller(eventUser, closureMethod))

  fun <T : Any, S : CallSource> add(eventUser : T, functionMethod : Function3<T, S, P1, Unit>) =
    addCaller(MethodCaller(eventUser, functionMethod))
}

// -------------------------------------------------------------------------
/**Listeners collection for type **(CallSource,P1,P2)->Unit**. Usage see in [jm_lib_EventTest.TestUser1]
 * @see EventBase*/
class Event2<in LT : Any, P1, P2>(listenerEventMethod : LT.(CallSource, P1, P2) -> Unit) : EventBase(listenerEventMethod) {

  fun call(Source : CallSource, p1 : P1, p2 : P2) =
    callMethods(Source, p1, p2)

  fun <T : Any> add(eventUser : T, listenerObject : LT) = addCaller(ListenerCaller(eventUser, listenerObject))

  fun <T : Any, S : CallSource> add(eventUser : T, closureMethod : Function3<S, P1, P2, Unit>) =
    addCaller(ClosureCaller(eventUser, closureMethod))

  fun <T : Any, S : CallSource> add(eventUser : T, functionMethod : Function4<T, S, P1, P2, Unit>) =
    addCaller(MethodCaller(eventUser, functionMethod))
}

// -------------------------------------------------------------------------
/**Listeners collection for type **(CallSource,P1,P2,P3)->Unit**. Usage see in [jm_lib_EventTest.TestUser1]
 * @see EventBase*/
class Event3<in LT : Any, P1, P2, P3>(listenerEventMethod : LT.(CallSource, P1, P2, P3) -> Unit) : EventBase(listenerEventMethod) {

  fun call(Source : CallSource, p1 : P1, p2 : P2, p3 : P3) =
    callMethods(Source, p1, p2, p3)

  fun <T : Any> add(eventUser : T, listenerObject : LT) = addCaller(ListenerCaller(eventUser, listenerObject))

  fun <T : Any, S : CallSource> add(eventUser : T, closureMethod : Function4<S, P1, P2, P3, Unit>) =
    addCaller(ClosureCaller(eventUser, closureMethod))

  fun <T : Any, S : CallSource> add(eventUser : T, functionMethod : Function5<T, S, P1, P2, P3, Unit>) =
    addCaller(MethodCaller(eventUser, functionMethod))
}

// -------------------------------------------------------------------------
/**Listeners collection for type **(CallSource,P1,P2,P3,P4)->Unit**. Usage see in [jm_lib_EventTest.TestUser1]
 * @see EventBase*/
class Event4<in LT : Any, P1, P2, P3, P4>(listenerEventMethod : LT.(CallSource, P1, P2, P3, P4) -> Unit) : EventBase(listenerEventMethod) {

  fun call(Source : CallSource, p1 : P1, p2 : P2, p3 : P3, p4 : P4) =
    callMethods(Source, p1, p2, p3, p4)

  fun <T : Any, S : CallSource> add(eventUser : T, closureMethod : Function5<S, P1, P2, P3, P4, Unit>) =
    addCaller(ClosureCaller(eventUser, closureMethod))

  fun <T : Any, S : CallSource> add(eventUser : T, functionMethod : Function6<T, S, P1, P2, P3, P4, Unit>) =
    addCaller(MethodCaller(eventUser, functionMethod))
}

// -------------------------------------------------------------------------
/**Listeners collection for type **(CallSource,P1,P2,P3,P4,P5)->Unit**. Usage see in [jm_lib_EventTest.TestUser1]
 * @see EventBase*/
class Event5<in LT : Any, P1, P2, P3, P4, P5>(listenerEventMethod : LT.(CallSource, P1, P2, P3, P4, P5) -> Unit) : EventBase(listenerEventMethod) {

  fun call(Source : CallSource, p1 : P1, p2 : P2, p3 : P3, p4 : P4, p5 : P5) =
    callMethods(Source, p1, p2, p3, p4, p5)

  fun <T : Any> add(eventUser : T, listenerObject : LT) = addCaller(ListenerCaller(eventUser, listenerObject))

  fun <T : Any, S : CallSource> add(eventUser : T, closureMethod : Function6<S, P1, P2, P3, P4, P5, Unit>) =
    addCaller(ClosureCaller(eventUser, closureMethod))

  fun <T : Any, S : CallSource> add(eventUser : T, functionMethod : Function7<T, S, P1, P2, P3, P4, P5, Unit>) =
    addCaller(MethodCaller(eventUser, functionMethod))
}

//------------------------------------------------------------------
//TEST
internal fun staticFunction(src : CallSource) {
  println("staticFunction: $src")
}

class jm_lib_EventTest {
  class Test : CallSource {

    //test interface used by listeners
    interface TCall {
      fun call(src : CallSource) {}
      fun position(src : CallSource, pos : Int) {}
    }

    //event holder for type: (CallSource)->Unit
    val OnChange = Event(TCall::call)
    val OnPosition = Event1(TCall::position)

    //notify all attached listeners
    fun F() {
      OnChange.call(this)
      OnPosition.call(this, 10)
    }
  }

  // -------------------------------------------------------------------------
  // VOID
  //class which set listeners
  class TestUserVoid(val t : Test) {

    companion object {
      fun staticInClass(src : CallSource) {
        println("staticInClass: $src")
      }
    }

    //class method
    fun idClassMethod(src : Test) {
      println("closure callback: $src")
    }

    //TEST entry
    fun F() {

      //add listener as class method
      t.OnChange.add(this, this::idClassMethod)

      //add listener as static method
      t.OnChange.add(this, Companion::staticInClass)

      //add listener as interface impl
      t.OnChange.add(this, object : Test.TCall {
        override fun call(src : CallSource) {
          println("listener: $src")
        }
      })

      //add listener as anon function
      val v = { it : Test -> println("lambdaVAR: $it") }
      t.OnChange.add(this, v)

      //add listener as lambda
      t.OnChange.add(this) { it : Test -> println("lambdaF: $it") }

      //emulate call listeners
      t.F()

      println("Listeners: ${t.OnChange.size}")
      t.OnChange -= this
      println("Listeners wo this: ${t.OnChange.size}")
    }
  }

  // -------------------------------------------------------------------------
  // INT
  class TestUser1(val t : Test) {
    companion object {
      fun staticInClass(src : CallSource, pos : Int) {
        println("staticInClass: $src ($pos)")
      }
    }

    fun idClassMethod(src : Test, pos : Int) {
      println("closure callback: $src ($pos)")
    }

    fun F() {
      t.OnPosition.add(this, TestUser1::idClassMethod)
      t.OnPosition.add(this, Companion::staticInClass)

      t.OnPosition.add(this, object : Test.TCall {
        override fun position(src : CallSource, pos : Int) {
          println("listener: $src ($pos)")
        }
      })

      val v = { it : Test, pos : Int ->
        println("lambdaVAR: $it ($pos)")
      }
      t.OnPosition.add(this, v)

      t.OnPosition.add(this) { it : Test, pos : Int ->
        println("lambdaF: $it ($pos)")
      }

      t.F()
      t.OnChange -= this
    }
  }

  companion object {
    fun TEST() {
      val t = Test()

      t.OnChange.add(this, ::staticFunction)

      println("VOID")
      TestUserVoid(t).F()

      println("INT")
      TestUser1(t).F()
    }
  }
}

