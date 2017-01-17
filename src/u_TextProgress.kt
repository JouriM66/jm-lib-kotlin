package jm.lib

/*Changes
 14.01.2017
   + TextProgress.abort() marked as return Throwable to allow use it as "throw abort()" to tell compiler all below code is not called

 09.01.2017
   * TextProgress - changed default progress to ConsoleProgress
   + ConsoleProgress - added static [instance] field
   + QuietProgress - added static [instance] field
*/

// -------------------------------------------------------------------------
/** User progress, warning and error information.
 *  Descendants must override at least [outs] and [note]
 */
open class TextProgress {
  @JvmField val period = Period(600)
  @JvmField var noError = false
  @JvmField var noWarning = false
  @JvmField var quiet = false
  @JvmField var unbuffered = false

  companion object {
    @JvmStatic private var FInstance : TextProgress? = null

    @JvmStatic fun instance(v : TextProgress? = null) : TextProgress {
      if (FInstance == null) FInstance = v ?: ConsoleProgress.instance
      return FInstance!!
    }
  }

  fun ParseArgs(a : Args) {
    if (a.CheckDel("q;quiet")) quiet = true
    if (a.CheckDel("no-err;no-error")) noError = true
    if (a.CheckDel("no-warn;no-warning")) noWarning = true
  }

  /**Write message to USER space WITHOUT header and \r\n*/
  open fun outs(f : String) : TextProgress = this

  /**Write formatted user message WITHOUT \r\n */
  open fun out(f : String, vararg args : Any?) =
    outs(f.format(*args))

  /**Write formatted user message WITH \r\n */
  open fun outn(f : String, vararg args : Any?) =
    outs((f + "\n").format(*args))

  /**Write message to SYSTEM space WITHOUT header and \r\n.
   * Descendants must check "quiet" and use it to supress output.
   */
  open fun note(f : String, vararg args : Any?) : TextProgress = this

  /**Display progress text*/
  open fun progress(nm : String, vararg s : Any?) =
    if (period.End()) note("%s\r", nm.format(*s).padEnd(77)) else this

  /**Display warning*/
  open fun warn(f : String, vararg args : Any?) : TextProgress =
    if (noWarning) note("WARNING: " + f + "\n", *args) else this

  /**Write error to stderr WITH \r\n*/
  open fun err(f : String, vararg args : Any?, ex : Exception? = null) : TextProgress {
    if (noError) return this
    note("ERROR: " + f + "\n", *args)
    if (ex != null) {
      note("Exception: ")
      ex.printStackTrace()
    }
    return this
  }

  /**Write error to stderr WITH \r\n and terminate program*/
  open fun abort(f : String = "", vararg args : Any?, ex : Exception? = null) : Throwable {
    noError = false
    if (f == "") note("Programm aborted.\n") else err(f, *args)
    ex?.printStackTrace()
    System.exit(0)
    throw Exception("")
  }
}

// -------------------------------------------------------------------------
/** [TextProgress] descendant to direct messages to [System.out] and [System.err].
 *  All system data will be directed to [System.err], user data to [System.out] to allow
 *  correct redirection.
 */
class ConsoleProgress : TextProgress() {
  companion object {
    val instance by lazy { newInstance() }

    @JvmStatic fun preRegister() = TextProgress.instance(instance)
    @JvmStatic fun newInstance() = ConsoleProgress()
  }

  override fun outs(f : String) : TextProgress {
    System.out.print(f)
    if (unbuffered) System.out.flush()
    return this
  }

  override fun note(f : String, vararg args : Any?) : TextProgress {
    if (!quiet) {
      System.err.printf(f, *args)
      if (unbuffered) System.err.flush()
    }
    return this
  }
}

// -------------------------------------------------------------------------
/** [TextProgress] descendant to supress all output.
 * NOTE: Its impossible to use it to get user data.
 */
class QuietProgress : TextProgress() {
  companion object {
    val instance by lazy { newInstance() }

    @JvmStatic fun preRegister() = instance(newInstance())
    @JvmStatic fun newInstance() = QuietProgress()
  }
}