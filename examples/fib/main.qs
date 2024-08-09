include "../std/include/stdio.qsh"
include "../std/include/stdlib.qsh"

def i32 fib(i32 n) {
    def i32 a = 0
    def i32 b = 1
    def i32 i = 0
    while (++i < n) {
        def x = a
        a = b
        b = x + a
    }
    return b
}

def i32 main(i32 argc, i8** argv) {
    if argc != 2 {
        puts("USAGE: fib <n>")
        return 1
    }

    def n = atoi(argv[1])
    def result = fib(n)

    printf("fib(%d) = %d\n", n, result)
    return 0
}
