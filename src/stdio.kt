package jm.lib

import java.io.File
import java.io.OutputStream
import java.nio.file.Paths

/* Changes
 16.01.2017
   + getch() added stupid version of "press next key" function, currently only allows to use Return (see help)
*/

// ---------------------------------------------------------------------------------------
class NullOutputStream : OutputStream {
  private constructor() : super()

  private fun nonev() {}
  override fun write(b : Int) = nonev()
  override fun write(b : ByteArray?) = nonev()
  override fun write(b : ByteArray?, off : Int, len : Int) = nonev()
  override fun flush() = nonev()
  override fun close() = nonev()

  companion object {
    val instance by lazy {
      NullOutputStream()
    }
  }
}

// -------------------------------------------------------------------------
/**Compare file items placing directories at start.
 *
 * **Usage:**
 *```
 *    File("Path/to/directory/with/files")
 *      ?.listFiles()
 *      ?.sortedWith(FileDirComparator())
 *```
 **/
class FileDirComparator : Comparator<File> {
  override fun compare(l : File, r : File) : Int {
    if (l.isDirectory == r.isDirectory) return l.compareTo(r)
    if (l.isDirectory && !r.isDirectory) return -1
    if (!l.isDirectory && r.isDirectory) return 1
    return l.compareTo(r)
  }
}

// -------------------------------------------------------------------------
val File.rootName : String get() = Paths.get(canonicalPath).root.toString()
val File.root : File get() = Paths.get(canonicalPath).root.toFile()

// -------------------------------------------------------------------------
/** Hrrrr!!!!  Stupid Java just CANT read single char from stream!
 *  Its impossible to turn echo off too :-//
 *
 *  So u can use this method only as "press Enter" method. Will ignore all other input.
 **/
fun getch() : Char {
  val v = System.`in`
  while( v.available() > 0 ) v.read()
  return v.read().toChar()
}

// -------------------------------------------------------------------------
