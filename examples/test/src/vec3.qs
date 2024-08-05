include "../../std/include/stdio.qsh"
include "../../std/include/stdmath.qsh"
include "../../std/include/stdutil.qsh"

include "../include/vec3.qsh"

vec3 = $(e0, e1, e2) {
    return $(i) {
        return switch i
            0: e0
            1: e1
            2: e2
            default: 0
    }
}

vec3_length_squared = $(v) { return v(0) * v(0) + v(1) * v(1) + v(2) * v(2) }

vec3_length = $(v) { return sqrt(vec3_length_squared(v)) }

vec3_add = $(a, b) { return vec3(a(0) + b(0), a(1) + b(1), a(2) + b(2)) }

vec3_sub = $(a, b) { return vec3(a(0) - b(0), a(1) - b(1), a(2) - b(2)) }

vec3_mul = $(a, b) { return vec3(a(0) * b, a(1) * b, a(2) * b) }

vec3_div = $(a, b) { return vec3_mul(a, 1 / b) }

vec3_cross = $(a, b) {
    return vec3(
        a(1) * b(2) - a(2) * b(1),
        a(2) * b(0) - a(0) * b(2),
        a(0) * b(1) - a(1) * b(0))
}

vec3_dot = $(a, b) { return a(0) * b(0) + a(1) * b(1) + a(2) * b(2) }

vec3_string = $(v) { return format("[ %f, %f, %f ]", v(0), v(1), v(2)) }
