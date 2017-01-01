package jm.lib

import java.io.Closeable
import java.io.Flushable

// ---------------------------------------------------------------------------------------
/**Universal safe converter to String.
 *
 * Exception will be thrown only if source is absolutelly non-convertable
 *
 * - NULL value will be converted as ""
 * - all ordinal types converted to string (see [Any.toString()][toString])
 * - Boolean treated as "true"/"false"
 * - for any type except simple types exception will be thrown
 */
fun Any?.asString() : String {
  if (this == null) return ""
  return when (this) {
    is Char    -> this.toString()
    is Byte    -> this.toString()
    is Short   -> this.toString()
    is Long    -> this.toString()
    is Int     -> this.toString()
    is Float   -> this.toString()
    is Double  -> this.toString()
    is String  -> this
    is Boolean -> if (this) "true" else "false"
    else       -> throw UnsupportedOperationException("Conversion to string is impossible for: $this")
  }
}

/**Universal safe converter to Ordinal type (Long)
 *
 * Exception will be thrown only if source is absolutelly non-convertable
 *
 * - NULL value will be converted as 0
 * - strings will be converted to number (see [AtoL] or exception thrown)
 * - Boolean treated as 1/0
 * - for any type except simple types exception will be thrown
 */
fun Any?.asOrdinal() : Long {
  if (this == null) return 0
  return when (this) {
    is Char    -> this.toLong()
    is Byte    -> this.toLong()
    is Short   -> this.toLong()
    is Long    -> this
    is Int     -> this.toLong()
    is Float   -> this.toLong()
    is Double  -> this.toLong()
    is String  -> AtoL(this)
    is Boolean -> if (this) 1 else 0
    else       -> throw UnsupportedOperationException("Conversion to ordinal is impossible for: $this")
  }
}

/**Universal safe converter to double type (Double)
 *
 * Exception will be thrown only if source is absolutelly non-convertable
 *
 * - NULL value will be converted as 0
 * - strings will be converted to number (see [String.toDouble()][toDouble] or exception thrown
 * - Boolean treated as 1/0
 * - for any type except simple types exception will be thrown
 */
fun Any?.asDouble() : Double {
  if (this == null) return 0.0
  return when (this) {
    is Char    -> this.toDouble()
    is Byte    -> this.toDouble()
    is Short   -> this.toDouble()
    is Long    -> this.toDouble()
    is Int     -> this.toDouble()
    is String  -> this.toDouble()
    is Float   -> this.toDouble()
    is Double  -> this
    is Boolean -> if (this) 1.0 else 0.0
    else       -> throw UnsupportedOperationException("Conversion to double is impossible for: $this")
  }
}

// ---------------------------------------------------------------------------------------
/**Call block and supress any exceptions thrown, call optional exception notifier on exception*/
inline fun safeCall(cb : () -> Unit, onExcept : ((ex : Exception) -> Unit)? = null ) {
  try {
    cb.invoke()
  } catch(ex : Exception) {
    onExcept?.invoke(ex)
  }
}
// ---------------------------------------------------------------------------------------
/**Class to hold value and allow to close it
 * ```
 * 1. free object data
 * val oH  = Holder( CreateBigDataObject() )
 * CallCodeUsingObjectData( oH.h )
 * oH.close() //will set object ref to null
 *
 * 2. Auto closeable
 * Holder( FileOutputStream(FileName) ).use{
 *   //code using stream dta
 * } //will close stream and set it ref to null
 * ```
 */
class Holder<T>(@JvmField var handle : T? = null) : Closeable, AutoCloseable {
  val h : T get() = handle!!

  fun set(v : T?, doclose : Boolean = true) {
    if (handle == v) return
    if (doclose && handle != null ) {
      (handle as? Flushable)?.flush()
      (handle as? Closeable)?.close() ?: (handle as? AutoCloseable)?.close()
    }
    handle = v
  }

  fun move(v : Holder<T>) = set(v, true)
  fun set(v : Holder<T>, move : Boolean = false) {
    set(v.handle)
    if (move) v.set(null, false)
  }

  override fun close() {
    set(null)
  }

  fun isNull() = handle == null
  fun isNotNull() = handle != null
}

// ---------------------------------------------------------------------------------------
/**Compare objects always BY REFERENCE, even if [equals] is overloaded.*/
fun Any?.equalsByRef( other:Any? ) = jHelper.CmpByRef(this,other)

// ---------------------------------------------------------------------------------------
