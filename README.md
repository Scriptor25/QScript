# QSCRIPT

## What is QScript?

QScript is - like almost every other project on this account - a programming language. It was originally only designed to be a simple, interpreted toy language, with very high-level capabilities, similar to Java. But when I realized there is a JNI binding for the LLVM-C-API it just made sense to use LLVM to take this thing up a level or two, or should I say, down? That's because now it is as low-level as a language can get, strictly typed and many more things you could wish from a programming language <3 ...

## Why another language?

Well... i dont know either. But this one was like a way to heal my inner self from the damage done by JavaScript, Python and some other languages I don't really like.

## Should I use it?

If you want to use it for a project: dont. Just for fun: definitly. Or maybe. More like dont either. The language "standard" is in a very unstable state, meaning I will add or remove features, change the syntax and other things on the go, what makes for a very bad coding experience if every time you did something it gets un-supported again.

## Examples

You can find some examples in the [examples](examples/) directory: there is the classical [fibonacci](examples/fib/) program, which computes the fibonacci number for a given input number; the other examples (just called [test](examples/test/)) is just a test project which will contain different things everytime I test something else.

## Usage

If you launch the project with no args, you can enter expressions in a shell (pls dont use its still wip). Otherwise you can input one or more input filenames and, prepended by ```-o``` you set the output filename or directory, depending on you only providing one input filename. So for example if you enter ```<qscript> myfile.qs -o myfile.o```, ```myfile.qs``` will get compiled and emitted to ```myfile.o```. On the other hand if you entered ```<qscript> file1.qs file2.qs file3.qs -o outdir```, the files will get compiled and emitted into ```outdir```, e.g. ```file1.qs -> outdir/file1.o```.
