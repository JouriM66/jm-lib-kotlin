package jm.lib

import java.io.FileInputStream
import java.util.*

// -------------------------------------------------------------------------
/**Class to process console command line switches.
 * While processing remove keys, so, after all, only non-keys parameters
 * or invalid keys stay in array.
 */
class Args : ArrayList<String> {
  constructor(a : Array<String>?, vararg s : String) {
    if (a != null) for (p in a) if (p.isNotEmpty()) AddParam(p)
    for (p in s) if (p.isNotEmpty()) AddParam(p)
  }

  // -------------------------------------------------------------------------
  private fun AddParamFile(fnm : String) {
    try {
      FileInputStream(fnm)
        .reader()
        .forEachLine {
          AddParam(it)
        }
    } catch(e : Exception) {
      TextProgress.instance().err("Error reading params file \"$fnm\"")
    }
  }

  private fun AddParam(v : String) {
    if (v[0] == '@') {
      AddParamFile(v.substring(1))
      return
    }
    if (v.length > 2 && v[1] == '@') {
      val ch = v[0]
      if ((ch == '\"' || ch == '\'') && v[v.length - 1] == ch) {
        val fnm = v.substring(2, v.length - 1)
        if (fnm.isNotBlank())
          AddParamFile(fnm)
        return
      }
    }
    add(v)
  }

  private fun findCheck(nm : List<String>) : Int {
    nm.map { "-$it" }
      .forEach { key ->
        forEachIndexed { n, it ->
          if (it.equals(key, true)) return n
        }
      }
    return -1
  }

  private fun findArg(nm : List<String>) : Pair<Int, String> {
    nm.map { "-$it=" }
      .forEach { key ->
        forEachIndexed { n, it ->
          if (it.startsWith(key, true))
            return Pair(n, it.substring(key.length))
        }
      }
    return Pair(-1, "")
  }

// -------------------------------------------------------------------------
  /**Check if key in form -NAME=VALUE exists, get value and remove key.
   * If key not exist, return default value.
   * NOTE: Scans keys with value only, if key in form "-NAME" exist skip it
   * @param nm - key name
   * @param def - default value
   * @see Arg
   * @see Check
   * @see CheckDel
   */
  fun ArgDel(nm : String, def : String) : String = Arg(nm, def, false)


  /**Check if key in form -NAME=VALUE exists, get value and optionally remove key.
   * If key not exist, return default value.
   * NOTE: Scans keys with value only, if key in form "-NAME" exist skip it
   * @param nm - key name
   * @param def - default value
   * @param remove - if set remove found key
   * @see ArgDel
   * @see Check
   * @see CheckDel
   */
  fun Arg(nm : String, def : String, remove : Boolean = true) : String {
    val idx = findArg(nm.split(';'))
    if (idx.first >= 0) {
      if (remove) removeAt(idx.first)
      return idx.second
    } else
      return def
  }

// -------------------------------------------------------------------------
  /**Check if key in form -NAME exist. Remove key if found.
   * @param nm - key name
   * @see Arg
   * @see Check
   */
  fun CheckDel(nm : String) = Check(nm, true)

  /**Check if key in form -NAME exist. Optionally remove it.
   * @param nm - key name
   * @see Arg
   * @see CheckDel
   */
  fun Check(nm : String, remove : Boolean = false) : Boolean {
    val idx = findCheck(nm.split(';'))
    if (idx >= 0) {
      if (remove) removeAt(idx)
      return true
    } else
      return false
  }

  // -------------------------------------------------------------------------
  fun GetParamsDel() = GetParams(true)

  fun GetParams(remove : Boolean = false) : List<String> {
    var idx = 0
    val rc = mutableListOf<String>()
    while( idx < size ) {
      val s = get(idx)
      if ( s[0] == '-' ) idx++ else {
        rc.add(s)
        if ( remove ) removeAt(idx) else idx++
      }
    }
    return rc
  }
}

