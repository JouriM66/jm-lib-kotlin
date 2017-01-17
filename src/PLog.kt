package jm.lib

import java.io.Closeable

// -------------------------------------------------------------------------
/**Class for indented methods call logging with transparent usage.
 * The only limitation in method usage is use of "return" keyword. To return data
 * from method **return@PROC** form must be used (see sample below).
 *
 * **Constructors**
 *
 * ``PLog(enabled : Boolean = true)``
 *
 * Create PLog object with enabled or disabled logging.
 * If logging is set *false* log output will be disabled.
 * If logging is disabled almost NO overhead added to code speed.
 *
 * **Methods**
 * - [PROC]``(nm : String, params : String = "", cb : PLog.() -> R)`` - start function body
 * - [log]``(t : String)`` - log message to current [TextProgress]
 * - [log]``( cb:()->String )`` - log message to current [TextProgress] using lazy inited string.
 * This method must be used for maximum performance.
 *
 * **Sample code**
 *```
 * class PLogTestClass {
 *   val log = PLog() //PLog object
 *
 *   //Method returns Int
 *   fun Call() = log.PROC("Call") {
 *     log{"item Call"}
 *     0
 *   }
 *   //Void method (returns Unit)
 *   fun F() = log.PROC("F") {
 *     log{"item F"}
 *     Call()
 *   }
 *   //Method returns String
 *   fun A() = log.PROC("A") {
 *     log("item bF")
 *     F()
 *     log{"item aF"}
 *     return@PROC "text"
 *   }
 * }
 *```
 * **Output**
 * ```
 * A() {
 *   item bF
 *   F() {
 *     item F
 *     Call() {
 *       item Call
 *     }
 *   }
 *   item aF
 * }
 * ```
 * @see TextProgress
 */

/* Changes
 [09.01.2017]
   - removed [open] from class and protected functions
   * [enabled] field replaced by [allowlog]
   * class renamed to [PLog]
*/

class PLog(private val allowlog : Boolean = true) : Closeable {
  private var indent = 0
  private val progress by lazy { TextProgress.instance() }

  private fun proc(nm : String, params : String) {
    log("$nm($params) {")
    indent++
  }

  override fun close() {
    indent = (indent - 1).max(0)
    if (allowlog) log("}")
  }

  /**Write log text using current [TextProgress.note] with current indention level.*/
  fun log( s:String ) =
    if (allowlog) progress.note(indent.indent() + s + "\n") else progress

  /**Write log text using current [TextProgress.note] with current indention level.
   * This method must be used for maximum performance.
   */
  fun log( cb:()->String ) =
    if (allowlog) progress.note(indent.indent() + cb() + "\n") else progress

  /**Main logging method. Used as head for all user methods.
   * Increases indent level, writes method start and end to log.
   *
   * The only limitation in method usage is use of "return" keyword. To return data
   * from method "return@PROC" form must be used (see [PLog] sample).
   *
   * Will log method call as:
   * ```
   * MethodName( MethodParameters) {
   * }
   * ```
   * @param nm Method name used in log text.
   * @param params Text with method parameters description or values.
   * @param cb Original method body.
   */
  fun <R> PROC(nm : String, params : String = "", cb : PLog.() -> R) : R =
    this.use {
      if (allowlog) proc(nm, params)
      this.cb()
    }
}

// -------------------------------------------------------------------------
class PLogTestClass {
  val log = PLog() //PLog object

  //Method returns Int
  fun Call() = log.PROC("Call") {
    log{"item Call"}
    0
  }

  //Void method (returns Unit)
  fun F() = log.PROC("F") {
    log{"item F"}
    Call()
  }

  //Method returns String
  fun A() = log.PROC("A") {
    log("item bF")
    F()
    log{"item aF"}
    return@PROC "text"
  }
}
