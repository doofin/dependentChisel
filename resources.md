# resources and references
Here are some resources and references that may be useful for understanding the project or for further research.

# Domain Specific Languages
## chisel

Chisel: Constructing Hardware in a Scala Embedded Language : https://people.eecs.berkeley.edu/~krste/papers/chisel-dac2012.pdf

Reusability is FIRRTL Ground: https://aspire.eecs.berkeley.edu/wp/wp-content/uploads/2017/11/Reusability-is-FIRRTL-Ground-Izraelevitz.pdf

## embedded DSL

we aims to use macros rather than monad based DSL because we want to utilize scala's module system

## IR

Chisel Roadmap: https://github.com/chipsalliance/chisel3/blob/master/ROADMAP.md suggest to use MLIR-based FIRRTL Compiler https://github.com/llvm/circt

## type system and macro theories:

Some refined types implementations and ideas in Scala 3:

https://iltotore.github.io/iron/docs/index.html

https://github.com/fthomas/refined

Using opaque types: https://contributors.scala-lang.org/t/poor-or-rich-mans-refinement-types-in-scala-3-x/4647/2

scala 3 theory : Type Soundness for Dependent Object Types (DOT)  https://www.cs.purdue.edu/homes/rompf/papers/rompf-oopsla16.pdf

### macros and metaprogramming
scala 3 macro : Multi-stage programming with generative and analytical macros https://dl.acm.org/doi/pdf/10.1145/3486609.3487203?

A Practical Unifcation of Multi-stage Programming and Macros  http://biboudis.github.io/papers/pcp-gpce18.pdf

idris Elaborator Reflection : https://davidchristiansen.dk/david-christiansen-phd.pdf and https://www.type-driven.org.uk/edwinb/papers/elab-reflection.pdf 

Scala 3 macros usage: https://docs.scala-lang.org/scala3/guides/macros/macros.html , https://www.youtube.com/watch?v=BbTZi8siN28

## dependent types application

Scala 3 library for TensorFlow with type-level checks (may provide inspiration).  NOTE: the library uses Dotty, i.e. the "old" version of Scala 3, so the syntax and features may differ from Scala 3 "proper":

https://github.com/MaximeKjaer/tf-dotty
 
https://maximekjaer.github.io/tf-dotty/docs/dotty

# Verification 
formal specification of RISC-V : https://github.com/riscv/sail-riscv

    Sail is a language for describing the instruction-set architecture (ISA) semantics of processors: the architectural specification of the behaviour of machine instructions

## other works
Clash is a hardware description language based on Haskell : https://clash-lang.org/


