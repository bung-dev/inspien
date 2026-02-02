package com.inspien.receiver.jdbc;

import java.sql.Statement;

public record BatchResult(
        int requestedRows,
        int successCount,
        int successKnownRows,
        int successUnknown,
        int zeroOrOther
) {
    public static BatchResult from(int[] result, int requestedRows) {
        if (result == null) {
            return new BatchResult(requestedRows, 0, 0, 0, 0);
        }

        int successKnown = 0;
        int successUnknown = 0;
        int zeroOrOther = 0;

        for (int v : result) {
            if (v > 0) successKnown += v;
            else if (v == Statement.SUCCESS_NO_INFO) successUnknown++;
            else zeroOrOther++;
        }

        int successCount = successKnown + successUnknown;
        return new BatchResult(requestedRows, successCount, successKnown, successUnknown, zeroOrOther);
    }
}
