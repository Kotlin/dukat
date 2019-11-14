
//The solution to this isn't pretty, but it's better than nothing!
function fibonacci(num) {
    if (num <= 1) return 1;

    return fibonacci(num - 1) + fibonacci(num - 2);
}