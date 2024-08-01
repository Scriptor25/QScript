def void(i8*, ?) printf

def i32(i32) fib = $(n) {
    def i32 a = 0
    def i32 b = 1
    def i32 i = 1
    while (i++ < n) {
        def i32 x = a
        a = b
        b = x + a
    }
    return b
}

def i32(i32, i8**) main = $(argc, argv) {
    def i32 n = 10
    def i32 result = fib(n)

    printf("fib(%d) = %d\n", n, result)

    return 0
}
