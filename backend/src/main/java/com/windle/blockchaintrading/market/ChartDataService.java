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

        List<CandleStickResponse> candleStickResponses = new ArrayList<>();

        // extract time by string
        switch (time){
            case "1S": candleStickResponses = tradeRepository.findCandlesticksByInterval(1);
            case "15M": candleStickResponses = tradeRepository.findCandlesticksByInterval(15*60);
            case "1H": candleStickResponses = tradeRepository.findCandlesticksByInterval(60*60);
            case "4H": candleStickResponses = tradeRepository.findCandlesticksByInterval(4*60*60);
            case "1D": candleStickResponses = tradeRepository.findCandlesticksByInterval(24*3600);
            case "1W": candleStickResponses = tradeRepository.findCandlesticksByInterval(7*24*3600);
        }

        return candleStickResponses;
    }


}
