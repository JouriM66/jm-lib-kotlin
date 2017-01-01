package jm.lib

// ---------------------------------------------------------------------------------------
/**Helper to check if specified amount of time is end
 *```
 * val Period per(500)
 * while( veryLongOperation ) {
 *   if ( per.End() ) Say_WeWorking_NotHungup()
 *   ...
 * }
 *```
 */
class Period(private val period : Long) {
  private var start : Long

  init {
    start = System.currentTimeMillis()
  }

  fun End() : Boolean {
    val e = System.currentTimeMillis()
    if (e - start > period) {
      start = e
      return true
    } else
      return false
  }

  fun Reset() {
    start = System.currentTimeMillis()
  }

  fun Period() : Long =
    System.currentTimeMillis() - start
}

