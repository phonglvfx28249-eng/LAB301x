package com.windle.blockchaintrading.market;

import com.windle.blockchaintrading.dto.response.CandleStickResponse;
import com.windle.blockchaintrading.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChartDataService {

    private final TradeRepository tradeRepository;

    @Autowired
    public ChartDataService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public List<CandleStickResponse> getCandleStickData(String time) {
        if (time == null) {
            throw new IllegalArgumentException("Timeframe interval cannot be null");
        }

        int seconds = switch (time.toUpperCase()) {
            case "1S"  -> 1;
            case "15M" -> 15 * 60;
            case "1H"  -> 60 * 60;
            case "4H"  -> 4 * 60 * 60;
            case "1D"  -> 24 * 3600;
            case "1W"  -> 7 * 24 * 3600;
            default    -> throw new IllegalArgumentException("Unsupported timeframe interval: " + time);
        };

        return tradeRepository.findCandlesticksByInterval(seconds);
    }


}
