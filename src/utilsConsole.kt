package jm.lib.console

import jm.lib.ConsoleProgress
import jm.lib.TextProgress

private class DefConOut {
  companion object {
    @JvmStatic private var FSay : TextProgress? = null

    val say : TextProgress get() {
      if ( FSay == null ) FSay = ConsoleProgress.preRegister()
      return FSay!!
    }
  }
}

// -------------------------------------------------------------------------
/**Write single string to stdout WITHOUT \r\n*/
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

/**Write message to stderr WITHOUT header and \r\n*/
fun note(f : String, vararg args : Any?) =
  DefConOut.say.note(f, *args)

/**Write progress message to stderr (progress messages placed in one line)*/
fun progress(f : String, vararg args : Any?) =
  DefConOut.say.progress(f, *args)

/**Write error to stderr WITH \r\n*/
fun err(f : String, vararg args : Any?,ex : Exception?=null) =
  DefConOut.say.err("ERROR: " + f + "\n", *args,ex)

/**Write error to stderr WITH \r\n and terminate program*/
fun abort(str : String = "", vararg args : Any?, ex : Exception? = null) =
  DefConOut.say.abort(str, *args, ex=ex)

// -------------------------------------------------------------------------
