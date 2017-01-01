package jm.lib

// -------------------------------------------------------------------------
/**Call function if string IS null or empty*/
inline fun String?.ifEmpty(cb : () -> String) : String = if (isNullOrEmpty()) cb() else this!!

/**Call function if string is NOT null or empty*/
inline fun String?.ifNotEmpty(cb : (it : String) -> String) : String = if (!isNullOrEmpty()) cb(this!!) else ""

// ---------------------------------------------------------------------------------------
/**Convert string to text by quoting all \n, \r and so on to char pairs '\'+'n' */
fun String?.toText() : String = Str2Text(this)

/**Convert string to text by quoting all \n, \r and so on to text pairs '\'+'n' */
fun Str2Text(p : String?) : String {
  if (p.isNullOrEmpty()) return ""
  val b = StringBuffer()
  p!!.forEach {
    when (it) {
      '\n' -> b.append("\\n")
      '\r' -> b.append("\\r")
      '\t' -> b.append("\\t")
      '\b' -> b.append("\\b")
//      '\'' -> b.append("\\\'")
      '\"' -> b.append("\\\"")
      '\\' -> b.append("\\\\")
      else -> b.append(it)
    }
  }
  return b.toString()
}

/**Create string by combining pairs '\'+'n' to \n and so on*/
fun String?.fromText() : String = Text2Str(this)

/**Create string by combining pairs '\'+'n' to \n and so on*/
fun Text2Str(p : String?) : String {
  if (p == null || p.isNullOrEmpty()) return ""
  val b = StringBuffer()
  var n = 0
  while (n < p.length) {
    var it = p[n++]
    if (it == '\\') {
      it = p[n++]
      when (it) {
        'n'  -> b.append("\n")
        'r'  -> b.append("\r")
        't'  -> b.append("\t")
        'b'  -> b.append("\b")
        '\'' -> b.append("\'")
        '\"' -> b.append("\"")
        '\\' -> b.append("\\")
        else -> b.append(it)
      }
    } else
      b.append(it)
  }
  return b.toString()
}

// ---------------------------------------------------------------------------------------
/**Make time string in format h:mm:ss.ms from milliseconds value*/
val Long.millisTimeStr : String get() {
  val ms = this.rem(1000)
  val v = this / 1000
  val h = v / 3600
  val m = v.rem(3600) / 60
  val s = v.rem(60)
  return "%d:%02d:%02d.%03d".format(h, m, s, ms)
}

// ---------------------------------------------------------------------------------------
/**Create string for bytes count in short form (in Kb, Mb and so on)*/
fun Long.formatCps() : String {
  val fmt_chars = "BKMGTPEZYABCDEF"

  var charnum = 0L
  var ref = 0L
  var vv = this

  while (vv >= 1000) {
    charnum++
    ref = vv % 1000
    vv /= 1000
  }

  if (charnum == 0L) return vv.toString()
  if (charnum < fmt_chars.length) return "%d.%03d%c".format(vv, ref, fmt_chars[charnum.toInt()])
  return "%d.%03d?".format(vv, ref)
}

/**Create string for bytes count in short form (in Kb, Mb and so on)*/
fun Int.formatCps() : String {
  val fmt_chars = "BKMGTPEZYABCDEF"

  var charnum = 0
  var ref = 0
  var vv = this

  while (vv >= 1000) {
    charnum++
    ref = vv % 1000
    vv /= 1000
  }

  if (charnum == 0) return vv.toString()
  if (charnum < fmt_chars.length) return "%d.%03d%c".format(vv, ref, fmt_chars[charnum])
  return "%d.%03d?".format(vv, ref)
}

// ---------------------------------------------------------------------------------------
/**Check if char is valid UTF16 character (based on xerses sources)*/
fun Char.isValidUTFChar() : Boolean {
  if (this >= '\u0000' && this <= '\u0008') return false
  if (this >= '\u000B' && this <= '\u000C') return false
  if (this >= '\u000E' && this <= '\u001F') return false
  if (this >= '\uD800' && this <= '\uDFFF') return false
  if (this >= '\uFFFE' && this <= '\uFFFF') return false
  return true
}

/**Remove all invalid UTF16 characters from string using Char.[isValidUTFChar].
 * */
fun CharSequence?.filterInvalidUTFChars() : CharSequence =
  this?.filter { it.isValidUTFChar() } ?: ""

// ---------------------------------------------------------------------------------------
/**Make indention string using number as level.
 * String is empty for 0 and ``2*space*number`` for all other values
 **/
fun Int.indent() : String {
  val cn = this.max(0)
  return when (cn) {
    0    -> ""
    1    -> "  "
    else -> {
      val ca = CharArray(cn * 2)
      ca.fill(' ')
      String(ca)
    }
  }
}

/**Make indention string using number as level.
 * Repeat given string number of times.
 **/
fun Int.indent(s : String) = s.repeat(this.max(0))

// ---------------------------------------------------------------------------------------
