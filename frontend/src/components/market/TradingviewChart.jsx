import React, { useEffect, useRef } from "react";
import { createChart, ColorType, CandlestickSeries } from "lightweight-charts";

export default function CandleChart({
                                        data = MOCK_W_USD_DATA,
                                        height = 300,
                                    }) {
    const containerRef = useRef(null);
    const chartRef = useRef(null);
    const seriesRef = useRef(null);

    // 1. INITIALIZE CHART (Runs ONLY once on mount or when height changes)
    useEffect(() => {
        if (!containerRef.current) return;

        const chart = createChart(containerRef.current, {
            autoSize: true,
            height,
            layout: {
                background: { type: ColorType.Solid, color: "transparent" },
                textColor: "#333333",
            },
            grid: {
                vertLines: { color: "rgba(0, 0, 0, 0.06)" },
                horzLines: { color: "rgba(0, 0, 0, 0.06)" },
            },
            timeScale: {
                borderColor: "rgba(0, 0, 0, 0.15)",
                timeVisible: true,     // Shows hh:mm:ss for short intervals
                secondsVisible: false,  // Hides seconds to keep X-axis clean
            },
            rightPriceScale: {
                borderColor: "rgba(0, 0, 0, 0.15)",
            },
            crosshair: {
                mode: 0,
            },
            localization: {
                timeFormatter: (timestamp) => {
                    // Handle both UNIX timestamp (seconds) and Date string formats
                    if (typeof timestamp === "number") {
                        return new Date(timestamp * 1000).toUTCString();
                    }
                    return timestamp;
                },
            },
        });

        const candleSeries = chart.addSeries(CandlestickSeries, {
            upColor: "#22a06b",
            downColor: "#e0574a",
            borderUpColor: "#22a06b",
            borderDownColor: "#e0574a",
            wickUpColor: "#22a06b",
            wickDownColor: "#e0574a",
        });

        chartRef.current = chart;
        seriesRef.current = candleSeries;

        return () => {
            chart.remove();
            chartRef.current = null;
            seriesRef.current = null;
        };
    }, [height]); // 👈 Notice 'data' is removed from dependencies!

    // 2. UPDATE DATA IN-PLACE (Runs every time new data arrives WITHOUT restarting chart)
    useEffect(() => {
        if (!seriesRef.current || !data || data.length === 0) return;

        // Efficiently update the series on the existing chart
        seriesRef.current.setData(data);

    }, [data]);

    return (
        <div
            ref={containerRef}
            className="w-full rounded-md overflow-hidden border border-gray-700/40"
            style={{ height, background: "transparent" }}
        />
    );
}

// Placeholder manual data for W/USD
export const MOCK_W_USD_DATA = [
    { time: "2026-06-25", open: 65.2, high: 66.8, low: 64.5, close: 66.1 },
    { time: "2026-06-26", open: 66.1, high: 67.9, low: 65.8, close: 67.4 },
    { time: "2026-06-27", open: 67.4, high: 68.3, low: 66.2, close: 66.6 },
    { time: "2026-06-28", open: 66.6, high: 67.0, low: 64.9, close: 65.3 },
    { time: "2026-06-29", open: 65.3, high: 66.4, low: 64.8, close: 66.0 },
    { time: "2026-06-30", open: 66.0, high: 68.5, low: 65.9, close: 68.2 },
    { time: "2026-07-01", open: 68.2, high: 69.1, low: 67.3, close: 67.6 },
    { time: "2026-07-02", open: 67.6, high: 67.9, low: 66.0, close: 66.3 },
    { time: "2026-07-03", open: 66.3, high: 67.5, low: 65.7, close: 67.2 },
    { time: "2026-07-04", open: 67.2, high: 70.0, low: 67.0, close: 69.5 },
];