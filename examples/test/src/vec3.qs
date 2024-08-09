include "../../std/include/stdio.qsh"
include "../../std/include/stdmath.qsh"
include "../../std/include/stdutil.qsh"

include "../include/vec3.qsh"

def vec3 vec3(f64 e0, f64 e1, f64 e2) { return { e0, e1, e2 } }

def f64 vec3_length_squared(vec3 v) { return v[0] * v[0] + v[1] * v[1] + v[2] * v[2] }

def f64 vec3_length(vec3 v) { return sqrt(vec3_length_squared(v)) }

def vec3 vec3_add(vec3 a, vec3 b) { return { a[0] + b[0], a[1] + b[1], a[2] + b[2] } }

def vec3 vec3_sub(vec3 a, vec3 b) { return { a[0] - b[0], a[1] - b[1], a[2] - b[2] } }

def vec3 vec3_mul(vec3 a, f64 b) { return { a[0] * b, a[1] * b, a[2] * b } }

def vec3 vec3_div(vec3 a, f64 b) { return vec3_mul(a, 1 / b) }

def vec3 vec3_cross(vec3 a, vec3 b) {
    return {
        a[1] * b[2] - a[2] * b[1],
        a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0]
    }
}

def f64 vec3_dot(vec3 a, vec3 b) { return a[0] * b[0] + a[1] * b[1] + a[2] * b[2] }

def i32 vec3_puts(vec3 v) { return printf("[ %f, %f, %f ]\n", v[0], v[1], v[2]) }
