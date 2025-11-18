# dependentChisel
This is an ongoing research project to bring dependent type features to hardware design in Chisel-like syntax, as an embedded DSL(eDSL) in Scala 3.

It originally started as Master thesis "Dependent Chisel: Statically-checked hardware designs based on Chisel and Scala 3", by Yuchen du in 2023
https://github.com/doofin/dependentChisel/blob/master/Msc_Thesis_yuchen.pdf


## Design
The main philosophy is to make it clean and simple, and avoid complex language features. The core intermediate representation is an algebraic AST, which is easy to analyze and transform.

**algebraic AST**

The layer of DSL translation:

Chisel-like syntax -> list of commands as stack data structure -> Algebraic AST (FIRRTL like IR in Scala ADT) 

when we have the algebraic AST, it's convenient to do various analysis, transformation and finally code generation to FIRRTL. This algebraic AST is similar to FIRRTL IR in Chisel, but Chisel seems to discourage direct manipulation of FIRRTL IR AST, while we embrace it.

**dependent types**

It uses partial dependent types in Scala 3 to provide early error message, which can be directly shown in IDE when writing code, allowing you to catch bitwidth mismatch at compile time.
## examples
examples like adder, etc. can be found in src/test/scala/dependentChisel/

some inline examples:

```scala
  class Adder1(using GlobalInfo) extends UserModule {
    val a = newIO[2](VarType.Input)
    val b = newIO[2](VarType.Input)
    val y = newIO[2](VarType.Output)

    y := a + b
  }
```
A parameterized adder with static guarante:

```scala
  class AdderParm[I <: Int: ValueOf](using GlobalInfo) extends UserModule {
    val a = newInput[I]("a")
    val b = newInput[I]("b")
    val y = newOutput[I]("y")

    y := a + b
  }
```

Instantiate those modules : 
```scala
  class AdderComb4(using parent: GlobalInfo) extends UserModule {

    val a = newInput[2]("a")
    val b = newInput[2]("b")
    val c = newInput[2]("c")
    val d = newInput[2]("d")
    val y = newOutput[2]("y")

    val m1 = newMod(new AdderParm[2])
    val m2 = newMod(new AdderParm[2])

    m1.a := a
    m1.b := b
    m2.a := c
    m2.b := d

    y := m1.y + m2.y
  }
```


## Interop with chisel
The current implementation is based on Chisel 3.5.1, which is used internally. There's no direct interop with the original Chisel, but you can probably use the generated FIRRTL for that.


Although scala 3 can invoke scala 2.13 libraries,chisel uses scala 2 macros different from scala 3 ,making it partially incompatible.

To fix the mismatch, there are several possible ways:

- Rewrite all macros and make everything compatible.
- Rewrite some macros and extend some base class.
- Write a new frontend and emit FIRRTL.

# compile and run
This is a sbt project for Scala 3

    run : `sbt run`
    compile : `sbt compile` 


many tests under src/test can be run by 
    sbt test
    
### IDE support

[Metals](https://scalameta.org/metals/) with vscode 

[IntelliJ](https://blog.jetbrains.com/scala/)

## theories and related work
related work : https://github.com/doofin/dependentChisel/blob/master/resources.md

similar projects:
- [zaozi] : https://github.com/sequencer/zaozi


## misc

with
  
    git ls-files | grep '\.scala$' | xargs wc -l

chisel has  60927 total loc
