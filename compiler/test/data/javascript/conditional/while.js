
module.exports = {
    product(arr) {
        let product

        let i = 0
        while (typeof arr[i++] == "number") {
            if (product == undefined) {
                product = 1
            }

            product *= arr[i - 1]
        }

        return product
    }
}