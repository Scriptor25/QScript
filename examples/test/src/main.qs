include "../../std/include/stdio.qsh"

include "../include/vec3.qsh"

def i32(i32, i8**) main = $(argc, argv) {
    def vec3 a = vec3(1, 0, 2)
    def vec3 b = vec3(3.1, 4.5, 0.1)
    def vec3 c = vec3_add(a, b)
    puts(vec3_string(c))
    return 0
}
