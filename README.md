# dependentChisel
use dependent types in Scala 3 to provide early error message for Chisel

## theories and related work
related work : https://github.com/doofin/dependentChisel/resources.md

# compile and run
This is a mill project for Scala 3

## Usage

This is a normal mill project. You can compile code with `mill examples.compile` and run it
with `mill examples.run`, `mill -i examples.console` will start a Scala 3 REPL.

### IDE support

It's recommended to either use [Metals](https://scalameta.org/metals/) with the
editor of your choice or [the Scala Plugin for
IntelliJ](https://blog.jetbrains.com/scala/).

## Using Scala 3 in an existing project

### build.sc

```scala
def scalaVersion = "3.2.1"
```

