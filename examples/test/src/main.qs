include "../../std/include/stdio.qsh"

include "../include/vec3.qsh"

def i32 main(i32 argc, i8** argv) {
    def a = vec3(1, 0, 2)
    def b = vec3(3.1, 4.5, 0.1)
    def c = vec3_add(a, b)
    vec3_puts(c)
    return 0
}
