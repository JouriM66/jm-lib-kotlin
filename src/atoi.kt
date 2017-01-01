package jm.lib

// ---------------------------------------------------------------------------------------
/**AtoL - converts String to Long
 * - default for NULL and empty strings
 * - true/false as 1/0
 * - #XXX - hex
 * - bXXX - binary
 * - 0XXX - octal
 * - for other formats see [java.lang.Long.parseLong]
 *
 * @param s string to convert
 * @param rad conversion radix (default 10)
 * @param def default value used if convertion not possible (default 0)
 * @return converted value or def
 */
fun AtoL(s : String?, rad : Int = 10, def : Long = 0) : Long {
  if (s == null || s.isEmpty()) return def

  if (!s[0].isDigit()) {
    try {
      return if (s.toBoolean() == true) 1 else 0
    } catch (ex : RuntimeException) {
    }

    //#XXX
    if (s[0] == '#')
      try {
        return java.lang.Long.parseLong(s.substring(1), 16)
      } catch (ex : RuntimeException) {
        return def
      }

    //bXXX
    if (s[0] == 'b')
      try {
        return java.lang.Long.parseLong(s.substring(1), 2)
      } catch (ex : RuntimeException) {
        return def
      }

    return def
  }

  //0xXXX
  if (s.length > 2 && s[0] == '0' && (s[1] == 'x' || s[1] == 'X'))
    try {
      return java.lang.Long.parseLong(s.substring(2), 16)
    } catch (ex : RuntimeException) {
      return def
    }

  //0XXX
  if (s.length > 1 && s[0] == '0' && s[1].isDigit())
    try {
      return java.lang.Long.parseLong(s, 8)
    } catch (ex : RuntimeException) {
      return def
    }

  //default
  try {
    return java.lang.Long.parseLong(s, rad)
  } catch(ex : RuntimeException) {
  }
  try {
    return s.toLong()
  } catch(ex : RuntimeException) {
  }
  return def
}

/**Calls [AtoL] to convert
 */
fun AtoI(s : String?, rad : Int = 10, def : Int = 0) = AtoL(s, rad, def.toLong()).toInt()

fun AtoF(s : String, def : Float = 0f) : Float {
  try {
    return java.lang.Float.parseFloat(s)
  } catch (ex : RuntimeException) {
    return def
  }
}

fun AtoLD(s : String, def : Double = 0.0) : Double {
  try {
    return java.lang.Double.parseDouble(s)
  } catch (ex : RuntimeException) {
    return def
  }
}

// ---------------------------------------------------------------------------------------
