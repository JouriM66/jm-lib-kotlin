package jm.lib.console

import jm.lib.ConsoleProgress
import jm.lib.TextProgress

/* Changes
 16.01.2017
   + added 'unbuffered' to TextProgress as attempt to make console usage more useful. No luck :(

 14.01.2017
   + abort() marked as return Throwable to allow use it as "throw abort()" to tell compiler all next code is not called

 09.01.2017
   * DefConOut - changed method to get ConsoleProgress
*/

private class DefConOut {
  companion object {
    @JvmStatic val say : TextProgress by lazy{ ConsoleProgress.instance }
  }
}

// -------------------------------------------------------------------------
/**Write single string to stdout WITHOUT \r\n
 *
 * **NOTE**
 * Stupid Java API not allow to flush data to stdout if its not ends with "\n"!
 * so attempts to output something like "text\r" will keep silent until buffer fill.
 **/
fun outs(str : String) =
  DefConOut.say.outs(str)

/**Write text to stdout WITHOUT \r\n*/
fun out(str : String, vararg args : Any?) =
  DefConOut.say.out(str, *args)

/**Write text to stdout WITH \r\n*/
fun outn(str : String, vararg args : Any?) =
  out(str + "\n", *args)

/**Write warning to stderr WITH \r\n*/
fun warn(f : String, vararg args : Any?) {
  DefConOut.say.warn("WARNING: " + f + "\n", *args)
}

/**Write message to stderr WITHOUT header and \r\n
 *
 * **NOTE**
 * Stupid Java API not allow to flush data to stdout if its not ends with "\n"!
 * so attempts to output something like "text\r" will keep silent until buffer fill.
 **/
fun note(f : String, vararg args : Any?) =
  DefConOut.say.note(f, *args)

/**Write progress message to stderr (progress messages placed in one line)*/
fun progress(f : String, vararg args : Any?) =
  DefConOut.say.progress(f, *args)

/**Write error to stderr WITH \r\n*/
fun err(f : String, vararg args : Any?,ex : Exception?=null) =
  DefConOut.say.err("ERROR: " + f + "\n", *args,ex)

/**Write error to stderr WITH \r\n and terminate program*/
fun abort(str : String = "", vararg args : Any?, ex : Exception? = null) : Throwable =
  DefConOut.say.abort(str, *args, ex = ex)

// -------------------------------------------------------------------------
