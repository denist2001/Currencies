# Simple native application for displaying live currency rates.

## Implementation details:
● The app should consist of a single screen with a table of currency rates. Each
row should display a currency pair and its current price with maximum four
decimal places. The data should update every 10 seconds.
● When the new price is higher than or equal to the previous one, it should be
displayed in green, otherwise in red.
For the purposes of this task you can use the following [API](https://pricing-staging.unleashedcapital.com/rates)
Making GET request to this URL will return the current rates for example:
```
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
```