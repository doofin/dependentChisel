/* try to lift assertion earlier into
https://github.com/riscv-boom/riscv-boom/search?q=assert
for example,in
src/main/scala/exu/issue-units/issue-slot.scala

assert (is_invalid || io.clear || io.kill, "trying to overwrite a valid issue slot.")

is_invalid ,etc is chisel bool,assert is also in chisel
 */
