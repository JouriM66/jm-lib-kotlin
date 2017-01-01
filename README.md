
JM Kotling library
------------------

This library is a set of util methods, extensions and and classes designed
to help create Java applications with Kotlin.

Library contains universal code which can be used with all
types of Java applications created for all platforms.

Currently used platforms are:
- Desktop JAR applications
- Android

Full documentation for each class and util is available in sources (accessible as JavaDOC by item name in any IDE supports JavaDOC).

## How to use
You can download or close this repository and use included IntelliJ IDEA project file "jm.iml" to add module with sources to your project or you can create project in your favorite IDE and just add all source files included.
 
*NOTE*: You can place Kotlin files in any directory you want but you **must** place Java files in paths acording package name, so place all Java helpers library contains in right places.  

## Contents

  * Classes and utils sets:
    * [Stdio extensions](#stdio) - related to stream IO.
    * [Atoi](#atoi) - numbers related utils.
    * [Math](#math) - related to any math.
    * [Logging](#logs) - related to format data for log output.
    * [Collections](#colections) - related to manipulate with collections.
    * [Text](#text) - text manipulation utils.
    * [Utils](#utils) - unsorted utils.
    * [Console](#console) - short wraps for currently selected **TextProgress** class.
  * Separate helper classes
    * ``Args`` - Class to process console command line switches.
    * ``LazyVal<T>`` - expanded lazy init
    * ``PropV, PropP, PropVP, PropPP`` - classes to use as delegates to emulate reach functional properties.
    * ``Period`` - class for time measure and periodical actions.
    * ``Flag`` - reach class for bit manipulation as separate fields.
    * ``Event, Event1, ...``  class to hold callback or listener.
    * ``ChangeableValue, ChangeableLazy`` - Class to hold a value and check if it was changed by assignment.
    * ``AnyVal, AnyVar, AnyOrdinal, AnyString, AnyDouble`` - classes to hold typed value and allow to assign different types to it with auto conversion.
    * ``TextProgress`` - Universal class can be used to output data on any console. 

### Stdio extensions

- ``NullOutputStream`` - class for ignore all output. 

### Atoi
- ``AtoL(s : String?, rad : Int = 10, def : Long = 0)``
- ``AtoI(s : String?, rad : Int = 10, def : Int = 0)``
- ``AtoF(s : String, def : Float = 0f)``
- ``AtoLD(s : String, def : Double = 0.0)``

### Math
Set of classes and extensions to simplify math routines and conversions for different number types. 
```
//Simple max\min syntax
val v = 0.max(event.y).min(height)

//Get random number in range 0..number-1
for( n in 10.random() )  
 ;
 
//Absolute diference between numbers
val dif = -3.diff(-2.1) //gets 0.9 
```

### Logging
- ``TextProgress`` - class for formatted output error, warning and progress messages.
- ``PLog`` - class for output log data in indented format with nearly **zero** overhead.

**Sample code**
```
 class PLogTestClass {
   val log = Plog() //PLog object

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
```
**Output**
```
A() {
  item bF
  F() {
    item F
    Call() {
      item Call
    }
  }
  item aF
}
```

### Collections

Collection extension:
- ``Collection.addAll( varargs )``
- ``Collection.addAllAt( index, varargs )``
- ``MutableMap<K, MutableList<T>>.addToList(key : K, value : T)``
- ``MutableMap<K, MutableSet<T>>.addToSet(key : K, value : T)``
- Iterators for ``Int`` and ``Long``

```
val count : Long
for( n in count )  //Will itterate in [0..count-1]
  //
```

### Text
- ``String?.ifEmpty(cb : () -> String) : String``
- ``String?.ifNotEmpty(cb : (it : String) -> String) : String``
- ``String?.toText()``  
- ``Str2Text(p : String?) : String``- Convert string to text by quoting all \n, \r and so on to text pairs '\'+'n'.
- ``String?.fromText() : String``
- ``Text2Str(p : String?) : String`` - Create string by combining pairs '\'+'n' to \n and so on.
- ``Long.millisTimeStr : String`` - Make time string in format h:mm:ss.ms from milliseconds value.
- ``Long.formatCps() : String`` - Create string for bytes count in short form (in Kb, Mb and so on).
- ``Char.isValidUTFChar() : Boolean`` - Very quick check if char is valid UTF16 character (based on xerses sources).

### Utils
- ``Any?.asString() : String`` - Universal safe converter to String.
- ``Any?.asOrdinal() : Long`` - Universal safe converter to Ordinal type (Long).
- ``Any?.asDouble() : Double`` - Universal safe converter to double type (Double).
- ``safeCall(cb : () -> Unit)`` - Call block and supress any exceptions thrown.
- ``Holder<T>`` - Class to hold value and allow to close or delete it.
- ``Any?.equalsByRef( other:Any? )`` - Compare objects always BY REFERENCE, even if ``equals`` is overloaded.

* Holder example: free object data
```
val oH  = Holder( CreateBigDataObject() )
CallCodeUsingObjectData( oH.h )
oH.close() //will set object ref to null
```
* Holder example: Auto closeable
```
Holder( FileOutputStream(FileName) ).use{
  //code using stream dta
} //will close stream and set it ref to null
```

### Console
Set of function: ``out, outn, warn, err, progress, note`` to be used as short call for currently used **TextProgress** class.