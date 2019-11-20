
//The solution to this isn't pretty, but it's better than nothing!
function fibonacci(num) {
    if (num <= 1) return 1;

    //Using double minus here, to show that this is a number (for plus this isn't possible)
    return fibonacci(num - 1) - -fibonacci(num - 2);
}

module.exports.fibonacci = fibonacci