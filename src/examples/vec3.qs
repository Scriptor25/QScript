include "io.qs"
include "math.qs"

use vec3 as f64(i32)

def vec3(f64, f64, f64) vec3 = $(e0, e1, e2) {
    return $(i) {
        return switch i
            0: e0
            1: e1
            2: e2
            default: 0
    }
}

def f64(vec3) vec3_length_squared = $(v) {
    return v(0) * v(0) + v(1) * v(1) + v(2) * v(2)
}

def f64(vec3) vec3_length = $(v) {
    return sqrt(vec3_length_squared(v))
}

def vec3(vec3, vec3) vec3_add = $(a, b) {
    return vec3(a(0) + b(0), a(1) + b(1), a(2) + b(2))
}

def vec3(vec3, vec3) vec3_sub = $(a, b) {
    return vec3(a(0) - b(0), a(1) - b(1), a(2) - b(2))
}

def vec3(vec3, f64) vec3_mul = $(a, b) {
    return vec3(a(0) * b, a(1) * b, a(2) * b)
}

def vec3(vec3, f64) vec3_div = $(a, b) {
    return vec3_mul(a, 1 / b)
}

def vec3(vec3, vec3) vec3_cross = $(a, b) {
    return vec3(
        a(1) * b(2) - a(2) * b(1),
        a(2) * b(0) - a(0) * b(2),
        a(0) * b(1) - a(1) * b(0))
}

def f64(vec3, vec3) vec3_dot = $(a, b) {
    return a(0) * b(0) + a(1) * b(1) + a(2) * b(2)
}

def i8*(vec3) vec3_string = $(v) {
    return format("[ %f, %f, %f ]", v(0), v(1), v(2))
}
