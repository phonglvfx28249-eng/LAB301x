function makeIntradayCandles(count, stepSeconds, startPrice) {
    const now = Math.floor(Date.now() / 1000);
    const start = now - count * stepSeconds;
    let price = startPrice;
    const candles = [];

    for (let i = 0; i < count; i++) {
        const time = start + i * stepSeconds;
        const open = price;
        const change = (Math.random() - 0.5) * 1.2;
        const close = Math.max(1, open + change);
        const high = Math.max(open, close) + Math.random() * 0.5;
        const low = Math.min(open, close) - Math.random() * 0.5;
        candles.push({ time, open, high, low, close: Number(close.toFixed(2)) });
        price = close;
    }
    return candles;
}

function makeDailyCandles(count, stepDays, startPrice) {
    const candles = [];
    let price = startPrice;
    const today = new Date();

    for (let i = count - 1; i >= 0; i--) {
        const d = new Date(today);
        d.setDate(d.getDate() - i * stepDays);
        const timeStr = d.toISOString().slice(0, 10); // "YYYY-MM-DD"

        const open = price;
        const change = (Math.random() - 0.5) * 4;
        const close = Math.max(1, open + change);
        const high = Math.max(open, close) + Math.random() * 1.5;
        const low = Math.min(open, close) - Math.random() * 1.5;
        candles.push({
            time: timeStr,
            open: Number(open.toFixed(2)),
            high: Number(high.toFixed(2)),
            low: Number(low.toFixed(2)),
            close: Number(close.toFixed(2)),
        });
        price = close;
    }
    return candles;
}

// Keys match the `value`s used in MarketPage's TIME_INTERVALS.
export const CANDLE_DATA_BY_INTERVAL = {
    "1S": makeIntradayCandles(120, 1, 67),      // 1 second candles
    "15": makeIntradayCandles(96, 15 * 60, 67), // 15 minute candles
    "60": makeIntradayCandles(72, 60 * 60, 67), // 1 hour candles
    "240": makeIntradayCandles(60, 4 * 60 * 60, 67), // 4 hour candles
    D: makeDailyCandles(60, 1, 67),             // 1 day candles
    W: makeDailyCandles(52, 7, 67),              // 1 week candles
};