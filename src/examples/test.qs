def f64(i32)(f64, f64, f64) vec3 = $(x, y, z) {
    return $(i) {
        return switch i
            0: x
            1: y
            2: z
            default: 0
    }
}

def f64(i32)(f64(i32), f64(i32)) add3 = $(a, b) {
    return vec3(a(0) + b(0), a(1) + b(1), a(2) + b(2))
}

def void(i8*, ?) printf

def i32(i32, i8**) main = $(argc, argv) {
    def f64(i32) a = vec3(1, 0, 2)
    def f64(i32) b = vec3(3.1, 4.5, 0.1)
    def f64(i32) result = add3(a, b)
    printf("result = (%f %f %f)\n", result(0), result(1), result(2))
    return 0
}
