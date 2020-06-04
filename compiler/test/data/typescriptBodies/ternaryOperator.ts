function f() {
    let i = 1;
    while (i <= 100) {
        console.log((i % 15 == 0) ? "FizzBuzz" : ((i % 3 == 0) ? "Fizz" : ((i % 5 == 0) ? "Buzz" : "")));
        i++
    }
}
