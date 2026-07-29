package com.windle.blockchaintrading.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record CandleStickResponse(Timestamp time,
                                  BigDecimal openPrice,
                                  BigDecimal highPrice,
                                  BigDecimal lowPrice,
                                  BigDecimal closePrice) {
}
