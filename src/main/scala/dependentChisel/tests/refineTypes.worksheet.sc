import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*

def log(x: Double :| Positive): Double =
  Math.log(x) // Used like a normal `Double`

log(1.0) //Automatically verified at compile time.
// log(-1.0) //Compile-time error: Should be strictly positive

val runtimeValue: Double = (-10 to 50).sum
log(runtimeValue.refine) //Explicitly refine your external values at runtime.

// runtimeValue.refineEither.map(log) //Use monadic style for functional validation
runtimeValue.refineEither[Positive].map(log) //More explicitly
