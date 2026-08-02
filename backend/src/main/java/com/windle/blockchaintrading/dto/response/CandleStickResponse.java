package com.windle.blockchaintrading.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record CandleStickResponse(LocalDateTime time,
                                  BigDecimal openPrice,
                                  BigDecimal highPrice,
                                  BigDecimal lowPrice,
                                  BigDecimal closePrice) {
}
