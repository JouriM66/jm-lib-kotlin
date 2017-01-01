package jm.lib

// ---------------------------------------------------------------------------------------
fun Int.min(v : Int) : Int = if (this > v) v else this

fun Int.min(v : Long) : Long = if (this > v) v else this.toLong()
fun Int.min(v : Float) : Float = if (this > v) v else this.toFloat()
fun Int.min(v : Double) : Double = if (this > v) v else this.toDouble()
fun Int.max(v : Int) : Int = if (this < v) v else this
fun Int.max(v : Long) : Long = if (this < v) v else this.toLong()
fun Int.max(v : Float) : Float = if (this < v) v else this.toFloat()
fun Int.max(v : Double) : Double = if (this < v) v else this.toDouble()

fun Long.min(v : Long) : Long = if (this > v) v else this
fun Long.min(v : Int) : Long = if (this > v) v.toLong() else this
fun Long.min(v : Float) : Float = if (this > v) v else this.toFloat()
fun Long.min(v : Double) : Double = if (this > v) v else this.toDouble()
fun Long.max(v : Long) : Long = if (this < v) v else this
fun Long.max(v : Int) : Long = if (this < v) v.toLong() else this
fun Long.max(v : Float) : Float = if (this < v) v else this.toFloat()
fun Long.max(v : Double) : Double = if (this < v) v else this.toDouble()

fun Double.min(v : Double) : Double = if (this > v) v else this
fun Double.min(v : Float) : Double = if (this > v) v.toDouble() else this
fun Double.min(v : Int) : Double = if (this > v) v.toDouble() else this
fun Double.max(v : Double) : Double = if (this < v) v else this
fun Double.max(v : Float) : Double = if (this < v) v.toDouble() else this
fun Double.max(v : Int) : Double = if (this < v) v.toDouble() else this

fun Float.min(v : Float) : Float = if (this > v) v else this
fun Float.min(v : Double) : Double = if (this > v) v else this.toDouble()
fun Float.min(v : Int) : Float = if (this > v) v.toFloat() else this
fun Float.max(v : Float) : Float = if (this < v) v else this
fun Float.max(v : Double) : Float = if (this < v) v.toFloat() else this
fun Float.max(v : Int) : Float = if (this < v) v.toFloat() else this

// ---------------------------------------------------------------------------------------
fun Float.diff(v : Float) = Math.abs(this - v)

fun Float.diff(v : Double) = Math.abs(this - v.toFloat())
fun Float.diff(v : Int) = Math.abs(this - v.toFloat())
fun Double.diff(v : Double) = Math.abs(this - v)
fun Double.diff(v : Float) = Math.abs(this - v.toDouble())
fun Double.diff(v : Int) = Math.abs(this - v.toDouble())

// ---------------------------------------------------------------------------------------
/**Generate random Int in range ``[0..this-1]``.
 * Base value must be ``>1`` or param exception will be thrown
 **/
fun Int.random() : Int {
  require(this > 1) { "Invalid random base" }
  if (this == 2) return 1 else
    return (this * Math.random()).toInt().min(this - 1)
}

