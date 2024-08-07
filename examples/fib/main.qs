include "../std/include/stdio.qsh"

def i32 fib(i32 n) {
    def i32 a = 0
    def i32 b = 1
    def i32 i = 1
    while (i++ < n) {
        def x = a
        a = b
        b = x + a
    }
    return b
}

def i32 main(i32 argc, i8** argv) {
    def i32 n = 10
    def result = fib(n)

    printf("fib(%d) = %d\n", n, result)
    return 0
}
