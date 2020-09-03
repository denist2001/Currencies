package com.codechallenge.currencies.data

/**
{
    "rates":[
        {
            "symbol":"EURUSD",
            "price":"1.230545"
        },
        {
            "symbol":"GBPUSD",
            "price":"1.4068450000000001"
        },
        {
            "symbol":"EURGBP",
            "price":"0.87468"
        },
        {
            "symbol":"GBPCHF",
            "price":"1.3400699999999999"
        }
    ]
}
 */
data class Response(
    val rates: ArrayList<Rate>?
)