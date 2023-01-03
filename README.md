# dependentChisel
use partial dependent types in Scala 3 to provide early error message for Chisel

## theories and related work
related work : https://github.com/doofin/dependentChisel/blob/master/resources.md

## interop with chisel

Although scala 3 can invoke scala 2.13 libraries,chisel uses scala 2 macros different from scala 3 ,making it partially incompatible.

here are some proposed ways :

    rewrite all macros and make everything compat
    rewrite some macros and extend some base class
    write a new frontend and emit firrtl

# compile and run
This is a sbt project for Scala 3

    run : `sbt run`
    compile : `sbt compile` 

### IDE support

[Metals](https://scalameta.org/metals/) with vscode 

[IntelliJ](https://blog.jetbrains.com/scala/)

## misc

with
  
    git ls-files | grep '\.scala$' | xargs wc -l

chisel has  60927 total loc