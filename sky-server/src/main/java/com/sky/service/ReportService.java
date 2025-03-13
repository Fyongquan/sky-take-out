package com.sky.service;

import com.sky.vo.TurnoverReportVO;

public interface ReportService {

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnoverStatistics(String begin, String end);
}
