# dependentChisel
use dependent types in Scala 3 to provide early error message for Chisel

## theories and related work
related work : https://github.com/doofin/dependentChisel/blob/master/resources.md

# compile and run
This is a mill project for Scala 3

    start a REPL `mill -i dep.console`
    run : `mill dep.run`
    compile : `mill dep.compile` 

### IDE support

[Metals](https://scalameta.org/metals/) with vscode : 

When in a Mill workspace use the "Switch Build Server" command from Metals .
then

    mill mill.bsp.BSP/install

[IntelliJ](https://blog.jetbrains.com/scala/)


### build setups in build.sc

```scala
def scalaVersion = "3.2.1"
```

more at https://com-lihaoyi.github.io/mill/mill/Library_Dependencies.html 
