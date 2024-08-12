include "../../std/include/stdio.qsh"

include "../include/vec3.qsh"

def i32 main(i32 argc, i8** argv) {
    def vec3(f64, f64, f64)* ctor = vec3

    def a = ctor(1, 0, 2)
    def b = ctor(3.1, 4.5, 0.1)
    def c = vec3_add(a, b)
    vec3_puts(c)
    return 0
}
